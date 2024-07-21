package com.kivi.huidada.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户答案表
 * @TableName user_answer
 */
@TableName(value ="user_answer")
@Data
public class UserAnswer implements Serializable {
    /**
     * 用户答案id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属试卷id
     */
    @TableField(value = "test_paper_id")
    private Long testPaperId;

    /**
     * 答案命中的评分结果id，有可能为null，因为采用ai评分
     */
    @TableField(value = "scoring_result_id")
    private Long scoringResultId;

    /**
     * 用户选择的打分策略，默认采用自定义打分策略，1表示ai评分
     */
    @TableField(value = "scoring_type")
    private Integer scoringType;

    /**
     * 用户答案（JSON 数组：[A,B,A,C...]）
     */
    @TableField(value = "choices")
    private String choices;

    /**
     * 答案得分，评分类题目会产生
     */
    @TableField(value = "score")
    private Integer score;

    /**
     * 作答人id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 答案结果名称
     */
    @TableField(value = "result_name")
    private String resultName;

    /**
     * 答案结果描述
     */
    @TableField(value = "result_desc")
    private String resultDesc;

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