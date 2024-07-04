package com.kivi.huidada.model.dto.user_answer;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAnswerQueryRequestDTO implements Serializable {
    /**
     * 用户答案id
     */
    private Long id;

    /**
     * 作答人id
     */
    private Long userId;

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

    private static final long serialVersionUID = 1L;
}
