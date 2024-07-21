package com.kivi.huidada.model.dto.test_paper;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiGenerateQuestionRequestDTO implements Serializable {

    /**
     * 试卷名称
     */
    private String testName;

    /**
     * 试卷描述
     */
    private String description;

    /**
     * 试卷类型，0表示打分类，1表示测评类
     */
    private Integer type;

    /**
     * 题目数量
     */
    private Integer questionCount;

    /**
     * 选项数量
     */
    private Integer optionCount;

    private static final long serialVersionUID = 1L;
}
