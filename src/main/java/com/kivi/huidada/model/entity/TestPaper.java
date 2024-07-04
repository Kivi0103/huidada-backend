package com.kivi.huidada.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 试卷表
 * @TableName test_paper
 */
@TableName(value ="test_paper")
@Data
public class TestPaper implements Serializable {
    /**
     * 试卷id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 试卷名称
     */
    @TableField(value = "test_name")
    private String testName;

    /**
     * 试卷描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 题目内容，每道题由题目、选项key，选项值构成
     */
    @TableField(value = "question_content")
    private String questionContent;

    /**
     * 是否为ai生成试卷题目，0表示自定义的试卷题目，1表示ai试卷
     */
    @TableField(value = "is_ai")
    private Integer isAi;

    /**
     * 试卷创建人id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 试卷创建人名称
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 试卷封面背景图，关联到cos存储地址
     */
    @TableField(value = "bg_picture")
    private String bgPicture;

    /**
     * 试卷类型，0表示打分类，1表示测评类
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 评分策略类型，0表示用户自定义的评分策略，1表示ai生成的评分策略
     */
    @TableField(value = "scoring_strategy")
    private Integer scoringStrategy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 逻辑删除、0表示未删除，1表示删除
     */
    @TableField(value = "is_delete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}