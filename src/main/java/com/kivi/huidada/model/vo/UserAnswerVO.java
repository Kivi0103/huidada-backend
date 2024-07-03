package com.kivi.huidada.model.vo;

import lombok.Data;

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
    private Long id;

    /**
     * 所属试卷id
     */
    private Long test_paper_id;

    /**
     * 答案命中的评分结果id，有可能为null，因为采用ai评分
     */
    private Long scoring_result_id;

    /**
     * 用户选择的打分策略，默认采用自定义打分策略，1表示ai评分
     */
    private Integer scoring_type;

    /**
     * 用户答案（JSON 数组：[A,B,A,C...]）
     */
    private String choices;

    /**
     * 答案得分，评分类题目会产生
     */
    private Integer score;

    /**
     * 作答人id
     */
    private Long user_id;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 更新时间
     */
    private Date update_time;

    private static final long serialVersionUID = 1L;
}