package com.kivi.huidada.model.vo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kivi.huidada.model.dto.test_paper.QuestionItem;
import com.kivi.huidada.model.entity.TestPaper;
import com.kivi.huidada.model.entity.User;
import com.kivi.huidada.model.entity.UserAnswer;
import com.kivi.huidada.service.TestPaperService;
import lombok.Data;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户答案表
 * @TableName user_answer
 */
@Data
public class UserAnswerVO implements Serializable {
    /**
     * 用户答案id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 所属试卷id
     */
    private Long testPaperId;

    /**
     * 答案命中的评分结果id，有可能为null，因为采用ai评分
     */
    private Long scoringResultId;

    /**
     * 用户选择的打分策略，默认采用自定义打分策略，1表示ai评分
     */
    private Integer scoringType;

    /**
     * 用户答案（JSON 数组：[A,B,A,C...]）
     */
    private String choices;

    /**
     * 答案得分，评分类题目会产生
     */
    private Integer score;

    /**
     * 答案结果名称
     */
    private String resultName;

    /**
     * 答案结果描述
     */
    private String resultDesc;

    /**
     * 作答人id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
     * 作答人姓名
     */
    private String answeredUserName;

    /**
     * 试卷名称
     */
    private String testPaperName;

    /**
     * 测试描述
     */
    private String testDesc;

    /**
     * 题目内容
     */
    private String questionContent;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 题目类型
     */
    private Integer testPaperType;

    /**
     * 评分策略
     */
    private Integer scoringStrategyType;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public static UserAnswerVO objToVo(UserAnswer userAnswer, TestPaper testPaper, User user) {
        if (userAnswer == null) {
            return null;
        }
        UserAnswerVO userAnswerVO = new UserAnswerVO();
        BeanUtil.copyProperties(userAnswer, userAnswerVO);
        userAnswerVO.setTestPaperName(testPaper.getTestName());
        userAnswerVO.setTestDesc(testPaper.getDescription());
        userAnswerVO.setQuestionContent(JSONUtil.toJsonStr(JSONUtil.toList(testPaper.getQuestionContent(), QuestionItem.class)));
        userAnswerVO.setAnsweredUserName(user.getUserName());
        userAnswerVO.setTestPaperType(testPaper.getType());
        userAnswerVO.setScoringStrategyType(testPaper.getScoringStrategyType());
        return userAnswerVO;
    }
}