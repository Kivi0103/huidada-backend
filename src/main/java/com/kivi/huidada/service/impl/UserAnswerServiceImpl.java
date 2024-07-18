package com.kivi.huidada.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kivi.huidada.model.dto.test_paper.QuestionItem;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public TestResultVO submitCustomAnswer(CommitUserChoiceRequestDTO answer, HttpServletRequest request) {
        // 这里可以采取通过试卷id查出试卷的测评类型到底是ai还是自定义，也可以通过前端直接传进来参数。这里我就以前端传进来的方式进行处理。
        List<String> choices = answer.getChoices();
        Long testPaperId = answer.getTestPaperId();
        // 根据试卷id取出所有测评结果;
        TestPaper testPaper = testPaperService.getById(testPaperId);
        List<QuestionItem> questionItems = JSONUtil.toList(testPaper.getQuestionContent(), QuestionItem.class);
        Integer type = answer.getType();
        User loginUser = userService.getLoginUser(request);
        if(type.equals(0)){
            return submitCustomScoringAnswer(testPaperId, choices,questionItems, loginUser);
        }else{
            return submitCustomTestAnswer(testPaperId,choices, questionItems, loginUser);
        }
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
        testResultVO.setCreateTime(userAnswer.getCreateTime());
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
        testResultVO.setCreateTime(userAnswer.getCreateTime());
        testResultVO.setResultName(mostHitResult.getResultName());
        return testResultVO;
    }
}




