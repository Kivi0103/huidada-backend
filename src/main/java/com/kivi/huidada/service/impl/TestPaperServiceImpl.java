package com.kivi.huidada.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kivi.huidada.common.ErrorCode;
import com.kivi.huidada.constant.CommonConstant;
import com.kivi.huidada.exception.BusinessException;
import com.kivi.huidada.manager.ZhiPuAiManager;
import com.kivi.huidada.model.dto.test_paper.*;
import com.kivi.huidada.model.entity.TestPaper;
import com.kivi.huidada.model.entity.User;
import com.kivi.huidada.model.vo.TestCountVO;
import com.kivi.huidada.model.vo.TestPaperVO;
import com.kivi.huidada.service.TestPaperService;
import com.kivi.huidada.mapper.TestPaperMapper;
import com.kivi.huidada.service.UserService;
import com.kivi.huidada.utils.SqlUtils;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
* @author Kivi
* @description 针对表【test_paper(试卷表)】的数据库操作Service实现
* @createDate 2024-07-03 14:49:05
*/
@Service
public class TestPaperServiceImpl extends ServiceImpl<TestPaperMapper, TestPaper>
    implements TestPaperService {
    @Resource
    private UserService userService;

    @Resource
    private ZhiPuAiManager zhiPuAiManager;

    @Resource
    private Scheduler vipScheduler;

    @Resource
    private TestPaperMapper testPaperMapper;

    /**
     * 获取查询条件
     * @param testPaperQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<TestPaper> getQueryWrapper(TestPaperQueryRequestDTO testPaperQueryRequest) {
        QueryWrapper<TestPaper> queryWrapper = new QueryWrapper<>();
        if (testPaperQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = testPaperQueryRequest.getId();
        String testName = testPaperQueryRequest.getTestName();
        String description = testPaperQueryRequest.getDescription();
        Integer isAi = testPaperQueryRequest.getIsAi();
        Long userId = testPaperQueryRequest.getUserId();
        Integer type = testPaperQueryRequest.getType();
        Integer reviewStatus = testPaperQueryRequest.getReviewStatus();
        String reviewMessage = testPaperQueryRequest.getReviewMessage();
        Integer scoringStrategyType = testPaperQueryRequest.getScoringStrategyType();
        String searchText = testPaperQueryRequest.getSearchText();
        String sortField = testPaperQueryRequest.getSortField();
        String sortOrder = testPaperQueryRequest.getSortOrder();
        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("test_name", searchText).or().like("description", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(testName), "test_name", testName);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.like(StringUtils.isNotBlank(reviewMessage), "review_message", reviewMessage);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(isAi), "is_ai", isAi);
        queryWrapper.eq(ObjectUtils.isNotEmpty(type), "type", type);
        queryWrapper.eq(ObjectUtils.isNotEmpty(scoringStrategyType), "scoring_strategy", scoringStrategyType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(reviewStatus), "review_status", reviewStatus);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_DESC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<TestPaperVO> getAppVOPage(Page<TestPaper> appPage, HttpServletRequest request) {
        List<TestPaper> testPaperList = appPage.getRecords();
        Page<TestPaperVO> testPaperVOPage = new Page<>(appPage.getCurrent(), appPage.getSize(), appPage.getTotal());
        if (CollUtil.isEmpty(testPaperList)) {
            return testPaperVOPage;
        }
        // 对象列表 => 封装对象列表
        List<TestPaperVO> testPaperVOList = testPaperList.stream().map(app -> {
            return TestPaperVO.objToVo(app);
        }).collect(Collectors.toList());
        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        testPaperVOPage.setRecords(testPaperVOList);
        return testPaperVOPage;
    }

    @Override
    public Long addTestPaper(TestPaperAddRequestDTO testPaperAddRequestDTO, HttpServletRequest request) {
        // 校验参数
        String testName = testPaperAddRequestDTO.getTestName();
        String description = testPaperAddRequestDTO.getDescription();
        List<QuestionItem> questionContent = testPaperAddRequestDTO.getQuestionContent();
        if( ObjectUtils.isEmpty(testName)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "测试名称不能为空");
        }
        if( ObjectUtils.isEmpty(description)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "测试描述不能为空");
        }
        if( ObjectUtils.isEmpty(questionContent)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不能为空");
        }
        // 将DTO对象转换为实体类
        User loginUser = userService.getLoginUser(request);
        TestPaper testPaper = new TestPaper();
        BeanUtils.copyProperties(testPaperAddRequestDTO, testPaper);
        testPaper.setQuestionContent(JSONUtil.toJsonStr(questionContent));
        testPaper.setId(testPaperAddRequestDTO.getId());
        testPaper.setUserId(loginUser.getId());
        testPaper.setUserName(loginUser.getUserName());
        // 如果数据库中存在id和testName相同的数据则更新，否则新增
        QueryWrapper<TestPaper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", testPaperAddRequestDTO.getId());
        queryWrapper.eq("test_name", testName);

        TestPaper existingTestPaper = this.getOne(queryWrapper);

        if (existingTestPaper != null) {
            // 更新现有记录
            testPaper.setId(existingTestPaper.getId());
            this.updateById(testPaper);
        } else {
            // 新增记录
            this.save(testPaper);
        }
        return testPaper.getId();
    }

    @Override
    public List<QuestionItem> aiGenerateQuestion(AiGenerateQuestionRequestDTO aiGenerateQuestionRequestDTO, HttpServletRequest request) {
        String userMessage = validAndGetUserMessage(aiGenerateQuestionRequestDTO, request);
        // 调用ai接口生成题目
        String aiQuestionContent = zhiPuAiManager.doUnStableRequest(userMessage, CommonConstant.AI_GENERATE_QUESTIONS_SYSTEM_MESSAGE);
        int start = aiQuestionContent.indexOf("[");
        int end = aiQuestionContent.lastIndexOf("]");
        String jsonStr = aiQuestionContent.substring(start, end + 1);
        // 转换为对象
        List<QuestionItem> questionItemList = JSONUtil.toList(jsonStr, QuestionItem.class);
        return questionItemList;
    }

    @Override
    public Boolean updateTestPaper(TestPaperUpdateRequestDTO testPaperUpdateRequestDTO, HttpServletRequest request) {
        UpdateWrapper<TestPaper> updateWrapper = getUpdateWrapper(testPaperUpdateRequestDTO);
        return this.update(updateWrapper);
    }

    @Override
    public UpdateWrapper<TestPaper> getUpdateWrapper(TestPaperUpdateRequestDTO testPaperUpdateRequestDTO) {
        UpdateWrapper<TestPaper> updateWrapper = new UpdateWrapper<>();
        if (testPaperUpdateRequestDTO == null) {
            return updateWrapper;
        }
        // 从对象中取值
        Long id = testPaperUpdateRequestDTO.getId();
        String testName = testPaperUpdateRequestDTO.getTestName();
        String description = testPaperUpdateRequestDTO.getDescription();
        List<QuestionItem> questionContent = testPaperUpdateRequestDTO.getQuestionContent();
        Integer isAi = testPaperUpdateRequestDTO.getIsAi();
        String bgPicture = testPaperUpdateRequestDTO.getBgPicture();
        Integer type = testPaperUpdateRequestDTO.getType();
        Integer scoringStrategyType = testPaperUpdateRequestDTO.getScoringStrategyType();
        // 补充需要的更新条件
        updateWrapper.eq("id", id);
        updateWrapper.set(ObjectUtils.isNotEmpty(testName), "test_name", testName);
        updateWrapper.set(ObjectUtils.isNotEmpty(description), "description", description);
        updateWrapper.set(ObjectUtils.isNotEmpty(isAi), "is_ai", isAi);
        updateWrapper.set(ObjectUtils.isNotEmpty(bgPicture), "bg_picture", bgPicture);
        updateWrapper.set(ObjectUtils.isNotEmpty(type), "type", type);
        updateWrapper.set(ObjectUtils.isNotEmpty(scoringStrategyType), "scoring_strategy", scoringStrategyType);
        return updateWrapper;
    }

    @Override
    public Boolean delete(Long id) {
        // 构建更新条件和更新内容
        UpdateWrapper<TestPaper> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("is_delete", 1);
        // 执行更新操作
        return this.update(updateWrapper);
    }

    @Override
    public String validAndGetUserMessage(AiGenerateQuestionRequestDTO aiGenerateQuestionRequestDTO, HttpServletRequest request) {
        // 校验参数
        String testName = aiGenerateQuestionRequestDTO.getTestName();
        String description = aiGenerateQuestionRequestDTO.getDescription();
        Integer type = aiGenerateQuestionRequestDTO.getType();
        Integer questionCount = aiGenerateQuestionRequestDTO.getQuestionCount();
        Integer optionCount = aiGenerateQuestionRequestDTO.getOptionCount();

        if( ObjectUtils.isEmpty(testName)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "测试名称不能为空");
        }
        if( ObjectUtils.isEmpty(description)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "测试描述不能为空");
        }
        if( ObjectUtils.isEmpty(type)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "测试类型不能为空");
        }
        if( ObjectUtils.isEmpty(questionCount)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目数量不能为空");
        }
        if(questionCount<1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目数量不能小于1");
        }
        if( ObjectUtils.isEmpty(optionCount)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "选项数量不能为空");
        }
        if(optionCount<2){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "选项数量不能小于2");
        }
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(aiGenerateQuestionRequestDTO.getTestName()).append("，\n");
        userMessage.append("【【【").append(aiGenerateQuestionRequestDTO.getDescription()).append("】】】，\n");
        userMessage.append("【【").append(aiGenerateQuestionRequestDTO.getQuestionCount()).append("】】").append("，\n");
        userMessage.append(aiGenerateQuestionRequestDTO.getOptionCount()).append("\n");
        return userMessage.toString();
    }

    /**
     * SSE 方式生成试题
     * @param aiGenerateQuestionRequestDTO
     * @param request
     * @return
     */
    @Override
    public SseEmitter aiGenerateQuestionSSE(AiGenerateQuestionRequestDTO aiGenerateQuestionRequestDTO, HttpServletRequest request) {
        // 校验参数
        String userMessage = validAndGetUserMessage(aiGenerateQuestionRequestDTO, request);
        // 建立 SSE 连接对象，0 表示永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        // AI 生成，SSE 流式返回
        Flowable<ModelData> modelDataFlowable = zhiPuAiManager.doStreamRequest(CommonConstant.AI_GENERATE_QUESTIONS_SYSTEM_MESSAGE, userMessage, null);
        // 左括号计数器，除了默认值外，当回归为 0 时，表示左括号等于右括号，可以截取
        AtomicInteger counter = new AtomicInteger(0);
        // 拼接完整题目
        StringBuilder stringBuilder = new StringBuilder();
        // 默认全局线程池
        Scheduler scheduler = Schedulers.io();
        modelDataFlowable
                .observeOn(scheduler)
                .map(modelData -> modelData.getChoices().get(0).getDelta().getContent())
                .map(message -> message.replaceAll("\\s", ""))
                .filter(StrUtil::isNotBlank)
                .flatMap(message -> {
                    List<Character> characterList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        characterList.add(c);
                    }
                    return Flowable.fromIterable(characterList);
                })
                .doOnNext(c -> {
                    // 如果是 '{'，计数器 + 1
                    if (c == '{') {
                        counter.addAndGet(1);
                    }
                    if (counter.get() > 0) {
                        stringBuilder.append(c);
                    }
                    if (c == '}') {
                        counter.addAndGet(-1);
                        if (counter.get() == 0) {
                            sseEmitter.send(JSONUtil.toJsonStr(stringBuilder.toString()));
                            // 重置，准备拼接下一道题
                            stringBuilder.setLength(0);
                        }
                    }
                })
                .doOnError((e) -> log.error("sse error", e))
                .doOnComplete(sseEmitter::complete)
                .subscribe();
        return sseEmitter;
    }

    @Override
    public List<TestCountVO> testCountTop10() {
        List<TestCountVO> testCountVOList = testPaperMapper.selectCountTop10();
        return testCountVOList;
    }

}




