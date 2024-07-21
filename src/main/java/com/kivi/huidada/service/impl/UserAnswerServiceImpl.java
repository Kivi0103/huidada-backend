package com.kivi.huidada.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kivi.huidada.constant.CommonConstant;
import com.kivi.huidada.manager.ZhiPuAiManager;
import com.kivi.huidada.model.dto.test_paper.QuestionItem;
import com.kivi.huidada.model.dto.user_answer.AiUserAnswerItem;
import com.kivi.huidada.model.dto.user_answer.CommitUserChoiceRequestDTO;
import com.kivi.huidada.model.entity.ScoringResult;
import com.kivi.huidada.model.entity.TestPaper;
import com.kivi.huidada.model.entity.User;
import com.kivi.huidada.model.entity.UserAnswer;
import com.kivi.huidada.model.enums.ScoringTypeEnum;
import com.kivi.huidada.model.vo.TestResultVO;
import com.kivi.huidada.model.vo.UserAnswerVO;
import com.kivi.huidada.service.ScoringResultService;
import com.kivi.huidada.service.TestPaperService;
import com.kivi.huidada.service.UserAnswerService;
import com.kivi.huidada.mapper.UserAnswerMapper;
import com.kivi.huidada.service.UserService;
import org.joda.time.DateTimeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    @Override
    public TestResultVO submitCustomAnswer(CommitUserChoiceRequestDTO answer, HttpServletRequest request) {
        // 这里可以采取通过试卷id查出试卷的测评类型到底是ai还是自定义，也可以通过前端直接传进来参数。这里我就以前端传进来的方式进行处理。
        List<String> choices = answer.getChoices();
        Long testPaperId = answer.getTestPaperId();
        Integer isAI = answer.getScoringStrategyType();
        // 根据试卷id取出所有测评结果;
        TestPaper testPaper = testPaperService.getById(testPaperId);
        List<QuestionItem> questionItems = JSONUtil.toList(testPaper.getQuestionContent(), QuestionItem.class);
        Integer type = answer.getType();
        User loginUser = userService.getLoginUser(request);
        if(isAI == 1){
            return submitAIScoringAnswer(testPaperId, choices, questionItems, loginUser);
        }else{
            if(type.equals(0)){
                return submitCustomScoringAnswer(testPaperId, choices,questionItems, loginUser);
            }else{
                return submitCustomTestAnswer(testPaperId,choices, questionItems, loginUser);
            }
        }
    }

    private TestResultVO submitAIScoringAnswer(Long testPaperId, List<String> choices, List<QuestionItem> questionItems, User loginUser) {
        // 生成ai测评的用户prompt
        // 1.取出每道题的题目描述和对应用户选择的选项描述添加到aiUserAnswerItemList中
        List<AiUserAnswerItem> aiUserAnswerItemList = new ArrayList<>();
        int i=0;
        for(QuestionItem questionItem : questionItems){
            String choice = choices.get(i);
            List<QuestionItem.Option> options = questionItem.getOptions();
            for(QuestionItem.Option option : options){
                if(option.getKey().equals(choice)){
                    AiUserAnswerItem aiUserAnswerItem = new AiUserAnswerItem();
                    aiUserAnswerItem.setQuestion(questionItem.getQuestionDesc());
                    aiUserAnswerItem.setAnswer(option.getOptionDesc());
                    aiUserAnswerItemList.add(aiUserAnswerItem);
                }
            }
            i++;
        }
        // 2.根据测试id取出测试名称和测试描述
        TestPaper testPaper = testPaperService.getById(testPaperId);;
        // 3.生成ai测评的用户prompt
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(testPaper.getTestName()).append("\n");
        userMessage.append("【【【").append(testPaper.getDescription()).append("】】】，\n");
        userMessage.append(JSONUtil.toJsonStr(aiUserAnswerItemList));
        // 4.调用ai接口获取ai测评结果
        String aiAnswer = zhiPuAiManager.doStableRequest(userMessage.toString(), CommonConstant.AI_GENERATE_ANSWER_SYSTEM_MESSAGE);
        // 5.解析ai测评结果，保存到数据库
        int start = aiAnswer.indexOf("{");
        int end =aiAnswer.lastIndexOf("}");
        String aiResultJson = aiAnswer.substring(start, end+1);
        TestResultVO testResultVO = JSONUtil.toBean(aiResultJson, TestResultVO.class);
        // 6.保存用户答案到数据库
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setTestPaperId(testPaperId);
        userAnswer.setResultName(testResultVO.getResultName());
        userAnswer.setResultDesc(testResultVO.getResultDesc());
        userAnswer.setScoringType(ScoringTypeEnum.AI_TEST.getValue());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setScore(testResultVO.getScore());
        userAnswer.setUserId(loginUser.getId());
        // 保存用户答案到数据库
        this.save(userAnswer);
        testResultVO.setCreateTime(new Date(DateTimeUtils.currentTimeMillis()));
        // 7.返回前端结果
        return testResultVO;
    }

    /**
     * 生成自定义的打分类测评结果
     * @param choices
     * @return
     */
    public TestResultVO submitCustomScoringAnswer(Long testPaperId,List<String> choices, List<QuestionItem> questionItems, User loginUser) {
        // 根据试卷id取出参考答案
        int sumScore = 0;
        int i=0;
        for(QuestionItem questionItem : questionItems){
            String choice = choices.get(i);
            List<QuestionItem.Option> options = questionItem.getOptions();
            for(QuestionItem.Option option : options){
                if(option.getKey().equals(choice)){
                    sumScore += option.getScore();
                }
            }
            i++;
        }
        System.out.println("sumScore: " + sumScore);
        // 查询所有的结果
        List<ScoringResult> scoringResults = scoringResultService.list(new QueryWrapper<ScoringResult>().eq("test_paper_id", testPaperId).orderBy(true, true, "result_score_range"));
        int k=1;
        for(;k<scoringResults.size();k++){
               if(scoringResults.get(k).getResultScoreRange() > sumScore){
                   break;
               }
        }
        ScoringResult scoringResult = scoringResults.get(k-1);
        // 保存用户答案
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setTestPaperId(testPaperId);
        userAnswer.setScoringResultId(scoringResult.getId());
        userAnswer.setScoringType(ScoringTypeEnum.CUSTOM_TEST.getValue());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setScore(sumScore);
        userAnswer.setUserId(loginUser.getId());
        // 保存用户答案到数据库
        this.save(userAnswer);
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
     * @param choices
     * @return
     */
    public TestResultVO submitCustomTestAnswer(Long testPaperId, List<String> choices, List<QuestionItem> questionItems, User loginUser) {
        // 统计选项中各个值的数量
        Map<String, Integer> optionCountMap = new HashMap<>();
        int i=0;
        for(QuestionItem questionItem : questionItems){
            List<QuestionItem.Option> options = questionItem.getOptions();
            for(QuestionItem.Option option : options){
                if(choices.get(i).equals(option.getKey())){
                    optionCountMap.put(option.getResult(), optionCountMap.getOrDefault(option.getResult(), 0) + 1);
                }
            }
            i++;
        }
        // 查询命中的结果
        List<ScoringResult> scoringResults = scoringResultService.list(new QueryWrapper<ScoringResult>().eq("test_paper_id", testPaperId));
        ScoringResult mostHitResult = null;
        int maxScore = 0;
        for(ScoringResult scoringResult : scoringResults){// 遍历测评结果
            int score = 0;
            List<String> resultProps = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            for(String resultProp : resultProps){
                score += optionCountMap.getOrDefault(resultProp, 0);
            }
            if(mostHitResult == null || score > maxScore){// 找到更匹配的结果，更新
                mostHitResult = scoringResult;
                maxScore = score;
            }
        }
        // 保存用户答案
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setTestPaperId(testPaperId);
        userAnswer.setScoringResultId(mostHitResult.getId());
        userAnswer.setScoringType(ScoringTypeEnum.CUSTOM_TEST.getValue());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setScore(maxScore);
        userAnswer.setUserId(loginUser.getId());
        // 保存用户答案到数据库
        this.save(userAnswer);
        // 封装前端返回结果
        TestResultVO testResultVO = new TestResultVO();
        testResultVO.setScore(maxScore);
        testResultVO.setResultDesc(mostHitResult.getResultDesc());
        testResultVO.setCreateTime(new Date(DateTimeUtils.currentTimeMillis()));
        testResultVO.setResultName(mostHitResult.getResultName());
        return testResultVO;
    }
}




