package com.kivi.huidada.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kivi.huidada.common.ErrorCode;
import com.kivi.huidada.constant.CommonConstant;
import com.kivi.huidada.exception.ThrowUtils;
import com.kivi.huidada.manager.ZhiPuAiManager;
import com.kivi.huidada.model.dto.test_paper.QuestionItem;
import com.kivi.huidada.model.dto.user_answer.AiUserAnswerItem;
import com.kivi.huidada.model.dto.user_answer.CommitUserChoiceRequestDTO;
import com.kivi.huidada.model.dto.user_answer.UserAnswerQueryRequestDTO;
import com.kivi.huidada.model.entity.ScoringResult;
import com.kivi.huidada.model.entity.TestPaper;
import com.kivi.huidada.model.entity.User;
import com.kivi.huidada.model.entity.UserAnswer;
import com.kivi.huidada.model.enums.ScoringTypeEnum;
import com.kivi.huidada.model.vo.AppAnswerResultCountVO;
import com.kivi.huidada.model.vo.TestPaperVO;
import com.kivi.huidada.model.vo.TestResultVO;
import com.kivi.huidada.model.vo.UserAnswerVO;
import com.kivi.huidada.service.ScoringResultService;
import com.kivi.huidada.service.TestPaperService;
import com.kivi.huidada.service.UserAnswerService;
import com.kivi.huidada.mapper.UserAnswerMapper;
import com.kivi.huidada.service.UserService;
import com.kivi.huidada.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Kivi
 * @description 针对表【user_answer(用户答案表)】的数据库操作Service实现
 * @createDate 2024-07-03 14:49:05
 */
@Service
public class UserAnswerServiceImpl extends ServiceImpl<UserAnswerMapper, UserAnswer>
        implements UserAnswerService {
    @Resource
    private ScoringResultService scoringResultService;
    @Resource
    private TestPaperService testPaperService;
    @Resource
    private UserService userService;
    @Resource
    private ZhiPuAiManager zhiPuAiManager;
    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserAnswerMapper userAnswerMapper;

    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";

    // 创建本地缓存，设置过期时间为一天
    private final Cache<String, String> answerCacheMap = Caffeine.newBuilder().initialCapacity(1024).expireAfterAccess(1, TimeUnit.DAYS).build();

    @Override
    public TestResultVO submitCustomAnswer(CommitUserChoiceRequestDTO answer, HttpServletRequest request) throws Exception {
        // 这里可以采取通过试卷id查出试卷的测评类型到底是ai还是自定义，也可以通过前端直接传进来参数。这里我就以前端传进来的方式进行处理。
        List<String> choices = answer.getChoices();
        Long testPaperId = answer.getTestPaperId();
        Integer isAI = answer.getScoringStrategyType();
        Long id = answer.getId();
        ThrowUtils.throwIf(id==null, ErrorCode.PARAMS_ERROR);
        // 根据试卷id取出所有测评结果;
        TestPaper testPaper = testPaperService.getById(testPaperId);
        List<QuestionItem> questionItems = JSONUtil.toList(testPaper.getQuestionContent(), QuestionItem.class);
        Integer type = answer.getType();
        User loginUser = userService.getLoginUser(request);
        if (isAI == 1) {
            return submitAIScoringAnswer(testPaperId, choices, questionItems, loginUser, id);
        } else {
            if (type.equals(0)) {
                return submitCustomScoringAnswer(testPaperId, choices, questionItems, loginUser, id);
            } else {
                return submitCustomTestAnswer(testPaperId, choices, questionItems, loginUser, id);
            }
        }
    }

    @Override
    public QueryWrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequestDTO userAnswerQueryRequestDTO) {
        QueryWrapper<UserAnswer> queryWrapper = new QueryWrapper<>();
        if (userAnswerQueryRequestDTO != null) {
            return queryWrapper;
        }
        Long id = userAnswerQueryRequestDTO.getId();
        Long userId = userAnswerQueryRequestDTO.getUserId();
        Long testPaperId = userAnswerQueryRequestDTO.getTestPaperId();
        Long scoringResultId = userAnswerQueryRequestDTO.getScoringResultId();
        Integer scoringType = userAnswerQueryRequestDTO.getScoringType();
        String sortField = userAnswerQueryRequestDTO.getSortField();
        String sortOrder = userAnswerQueryRequestDTO.getSortOrder();
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(testPaperId), "test_paper_id", testPaperId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(scoringResultId), "scoring_result_id", scoringResultId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(scoringType), "scoring_type", scoringType);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_DESC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> userAnswerPage, HttpServletRequest request) {
        List<UserAnswer> userAnswerList = userAnswerPage.getRecords();
        Page<UserAnswerVO> userAnswerVOPage = new Page<>(userAnswerPage.getCurrent(), userAnswerPage.getSize(), userAnswerPage.getTotal());
        if (CollUtil.isEmpty(userAnswerList)) {
            return userAnswerVOPage;
        }
        // 对象列表 => 封装对象列表
        List<UserAnswerVO> userAnswerVOList = userAnswerList.stream().map(userAnswer -> {
            TestPaper testPaper = testPaperService.getById(userAnswer.getTestPaperId());
            User user = userService.getById(userAnswer.getUserId());
            UserAnswerVO userAnswerVO = UserAnswerVO.objToVo(userAnswer, testPaper, user);
            return userAnswerVO;
        }).collect(Collectors.toList());
        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        userAnswerVOPage.setRecords(userAnswerVOList);
        return userAnswerVOPage;
    }

    @Override
    public List<AppAnswerResultCountVO> userAnswerCuntByTestPaperId(Long testPaperId) {
        return userAnswerMapper.userAnswerCuntByTestPaperId(testPaperId);
    }

    private TestResultVO submitAIScoringAnswer(Long testPaperId, List<String> choices, List<QuestionItem> questionItems, User loginUser, Long id) throws Exception {
        // 首先从缓存中获得答案
        String cacheKey = getCacheKey(testPaperId, choices);
        String aiResultJson = null;
        aiResultJson = answerCacheMap.getIfPresent(cacheKey);
        if(!StringUtils.isNotBlank(aiResultJson)){
            // 定义锁,使用cacheKey指定锁的名称，最细粒度的锁
            RLock lock = redissonClient.getLock(AI_ANSWER_LOCK + cacheKey);
            try{
                // 尝试获取锁，最多等待3秒，上锁以后10秒自动过期
                boolean isLock = lock.tryLock(3,10, TimeUnit.SECONDS);
                // 没抢到锁，抛出异常
                if (!isLock) {
                    ThrowUtils.throwIf(!isLock, ErrorCode.SYSTEM_ERROR);
                }
                // 抢到锁了，执行后续业务逻辑
                // 生成ai测评的用户prompt
                // 1.取出每道题的题目描述和对应用户选择的选项描述添加到aiUserAnswerItemList中
                List<AiUserAnswerItem> aiUserAnswerItemList = new ArrayList<>();
                int i = 0;
                for (QuestionItem questionItem : questionItems) {
                    String choice = choices.get(i);
                    List<QuestionItem.Option> options = questionItem.getOptions();
                    for (QuestionItem.Option option : options) {
                        if (option.getKey().equals(choice)) {
                            AiUserAnswerItem aiUserAnswerItem = new AiUserAnswerItem();
                            aiUserAnswerItem.setQuestion(questionItem.getQuestionDesc());
                            aiUserAnswerItem.setAnswer(option.getOptionDesc());
                            aiUserAnswerItemList.add(aiUserAnswerItem);
                        }
                    }
                    i++;
                }
                // 2.根据测试id取出测试名称和测试描述
                TestPaper testPaper = testPaperService.getById(testPaperId);
                ;
                // 3.生成ai测评的用户prompt
                StringBuilder userMessage = new StringBuilder();
                userMessage.append(testPaper.getTestName()).append("\n");
                userMessage.append("【【【").append(testPaper.getDescription()).append("】】】，\n");
                userMessage.append(JSONUtil.toJsonStr(aiUserAnswerItemList));
                // 4.调用ai接口获取ai测评结果
                String aiAnswer = zhiPuAiManager.doStableRequest(userMessage.toString(), CommonConstant.AI_GENERATE_ANSWER_SYSTEM_MESSAGE);
                // 5.解析ai测评结果，保存到数据库
                int start = aiAnswer.indexOf("{");
                int end = aiAnswer.lastIndexOf("}");
                aiResultJson = aiAnswer.substring(start, end + 1);
                // 6.保存ai测评结果到缓存
                answerCacheMap.put(cacheKey, aiResultJson);
            }finally {
                // 释放锁
                if (lock != null && lock.isLocked()) {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
        }
        TestResultVO testResultVO = JSONUtil.toBean(aiResultJson, TestResultVO.class);
        // 6.保存用户答案到数据库
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setId(id);
        userAnswer.setTestPaperId(testPaperId);
        userAnswer.setResultName(testResultVO.getResultName());
        userAnswer.setResultDesc(testResultVO.getResultDesc());
        userAnswer.setScoringType(ScoringTypeEnum.AI_TEST.getValue());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setScore(testResultVO.getScore());
        userAnswer.setUserId(loginUser.getId());
        // 写入数据库
        try {
            boolean result = this.save(userAnswer);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        } catch (DuplicateKeyException e) {
            // ignore error
        }
        testResultVO.setCreateTime(new Date(DateTimeUtils.currentTimeMillis()));
        // 7.返回前端结果
        return testResultVO;
    }

    /**
     * 生成自定义的打分类测评结果
     *
     * @param choices
     * @return
     */
    public TestResultVO submitCustomScoringAnswer(Long testPaperId, List<String> choices, List<QuestionItem> questionItems, User loginUser, Long id) {
        // 根据试卷id取出参考答案
        int sumScore = 0;
        int i = 0;
        for (QuestionItem questionItem : questionItems) {
            String choice = choices.get(i);
            List<QuestionItem.Option> options = questionItem.getOptions();
            for (QuestionItem.Option option : options) {
                if (option.getKey().equals(choice)) {
                    sumScore += option.getScore();
                }
            }
            i++;
        }
        System.out.println("sumScore: " + sumScore);
        // 查询所有的结果
        List<ScoringResult> scoringResults = scoringResultService.list(new QueryWrapper<ScoringResult>().eq("test_paper_id", testPaperId).orderBy(true, true, "result_score_range"));
        int k = 1;
        for (; k < scoringResults.size(); k++) {
            if (scoringResults.get(k).getResultScoreRange() > sumScore) {
                break;
            }
        }
        ScoringResult scoringResult = scoringResults.get(k - 1);
        // 保存用户答案
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setId(id);
        userAnswer.setTestPaperId(testPaperId);
        userAnswer.setScoringResultId(scoringResult.getId());
        userAnswer.setScoringType(ScoringTypeEnum.CUSTOM_TEST.getValue());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setScore(sumScore);
        userAnswer.setUserId(loginUser.getId());
        userAnswer.setResultName(scoringResult.getResultName());
        userAnswer.setResultDesc(scoringResult.getResultDesc());
        // 写入数据库
        try {
            boolean result = this.save(userAnswer);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        } catch (DuplicateKeyException e) {
            // ignore error
        }
        // 封装前端返回结果
        TestResultVO testResultVO = new TestResultVO();
        testResultVO.setScore(sumScore);
        testResultVO.setResultDesc(scoringResult.getResultDesc());
        // 获得当前时间的Date对象
        testResultVO.setCreateTime(new Date(DateTimeUtils.currentTimeMillis()));
        testResultVO.setResultName(scoringResult.getResultName());
        return testResultVO;
    }


    /**
     * 生成自定义的测评类测评结果
     *
     * @param choices
     * @return
     */
    public TestResultVO submitCustomTestAnswer(Long testPaperId, List<String> choices, List<QuestionItem> questionItems, User loginUser, Long id) {
        // 统计选项中各个值的数量
        Map<String, Integer> optionCountMap = new HashMap<>();
        int i = 0;
        for (QuestionItem questionItem : questionItems) {
            List<QuestionItem.Option> options = questionItem.getOptions();
            for (QuestionItem.Option option : options) {
                if (choices.get(i).equals(option.getKey())) {
                    optionCountMap.put(option.getResult(), optionCountMap.getOrDefault(option.getResult(), 0) + 1);
                }
            }
            i++;
        }
        // 查询命中的结果
        List<ScoringResult> scoringResults = scoringResultService.list(new QueryWrapper<ScoringResult>().eq("test_paper_id", testPaperId));
        ScoringResult mostHitResult = null;
        int maxScore = 0;
        for (ScoringResult scoringResult : scoringResults) {// 遍历测评结果
            int score = 0;
            List<String> resultProps = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            for (String resultProp : resultProps) {
                score += optionCountMap.getOrDefault(resultProp, 0);
            }
            if (mostHitResult == null || score > maxScore) {// 找到更匹配的结果，更新
                mostHitResult = scoringResult;
                maxScore = score;
            }
        }
        // 保存用户答案
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setId(id);
        userAnswer.setTestPaperId(testPaperId);
        userAnswer.setScoringResultId(mostHitResult.getId());
        userAnswer.setScoringType(ScoringTypeEnum.CUSTOM_TEST.getValue());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setScore(maxScore);
        userAnswer.setUserId(loginUser.getId());
        // 写入数据库
        try {
            boolean result = this.save(userAnswer);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        } catch (DuplicateKeyException e) {
            // ignore error
        }
        // 封装前端返回结果
        TestResultVO testResultVO = new TestResultVO();
        testResultVO.setScore(maxScore);
        testResultVO.setResultDesc(mostHitResult.getResultDesc());
        testResultVO.setCreateTime(new Date(DateTimeUtils.currentTimeMillis()));
        testResultVO.setResultName(mostHitResult.getResultName());
        return testResultVO;
    }

    /**
     * 获取缓存key
     */
    public String getCacheKey(Long testPaperId, List<String> choices) {
        return DigestUtil.md5Hex(testPaperId+":"+JSONUtil.toJsonStr(choices));
    }
}




