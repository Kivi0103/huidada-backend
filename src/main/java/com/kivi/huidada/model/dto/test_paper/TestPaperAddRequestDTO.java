package com.kivi.huidada.model.dto.test_paper;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TestPaperAddRequestDTO implements Serializable {

    /**
     * 试卷名称
     */
    private String test_name;

    /**
     * 试卷描述
     */
    private String description;

    /**
     * 题目内容，每道题由题目、选项key，选项值构成
     */
    private List<String > question_content;

    /**
     * 是否为ai生成试卷题目，0表示自定义的试卷题目，1表示ai试卷
     */
    private Integer is_ai;

    /**
     * 试卷封面背景图，关联到cos存储地址
     */
    private String bg_picture;

    /**
     * 试卷类型，0表示打分类，1表示测评类
     */
    private Integer type;

    /**
     * 评分策略类型，0表示用户自定义的评分策略，1表示ai生成的评分策略
     */
    private Integer scoring_strategy;

    private static final long serialVersionUID = 1L;
}
