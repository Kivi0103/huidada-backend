package com.kivi.huidada.model.dto.test_paper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kivi.huidada.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class TestPaperQueryRequestDTO extends PageRequest implements Serializable {
    /**
     * 试卷id
     */
    
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 试卷名称
     */
    private String testName;

    /**
     * 试卷描述
     */
    private String description;

    /**
     * 是否为ai生成试卷题目，0表示自定义的试卷题目，1表示ai试卷
     */
    private Integer isAi;

    /**
     * 试卷类型，0表示打分类，1表示测评类
     */
    private Integer type;

    /**
     * 审核状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 评分策略类型，0表示用户自定义的评分策略，1表示ai生成的评分策略
     */
    private Integer scoringStrategyType;

    /**
     * 搜索词
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
}
