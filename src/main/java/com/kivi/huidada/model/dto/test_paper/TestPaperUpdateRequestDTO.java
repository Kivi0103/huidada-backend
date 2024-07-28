package com.kivi.huidada.model.dto.test_paper;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TestPaperUpdateRequestDTO implements Serializable {
    /**
     * 试卷id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 试卷名称
     */
    private String testName;

    /**
     * 试卷描述
     */
    private String description;

    /**
     * 题目内容，每道题由题目、选项key，选项值构成
     */
    private List<QuestionItem> questionContent;

    /**
     * 是否为ai生成试卷题目，0表示自定义的试卷题目，1表示ai试卷
     */
    private Integer isAi;

    /**
     * 试卷封面背景图，关联到cos存储地址
     */
    private String bgPicture;

    /**
     * 试卷类型，0表示打分类，1表示测评类
     */
    private Integer type;

    /**
     * 评分策略类型，0表示用户自定义的评分策略，1表示ai生成的评分策略
     */
    private Integer scoringStrategyType;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}
