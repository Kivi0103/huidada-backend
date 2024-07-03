package com.kivi.huidada.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评分结果表
 * @TableName scoring_result
 */
@TableName(value ="scoring_result")
@Data
public class ScoringResult implements Serializable {
    /**
     * 评分策略id
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private Long id;

    /**
     * 结果名称
     */
    @TableField(value = "result_name")
    private String result_name;

    /**
     * 结果描述
     */
    @TableField(value = "result_desc")
    private String result_desc;

    /**
     * 结果属性集合 JSON，如 [I,S,T,J]，用于测评类试卷的匹配
     */
    @TableField(value = "result_prop")
    private String result_prop;

    /**
     * 结果图片、创建用户上传、存在cos中的地址
     */
    @TableField(value = "result_picture")
    private String result_picture;

    /**
     * 结果得分范围，用于打分类试卷匹配结果，如 80，表示 80及以上的分数命中此结果
     */
    @TableField(value = "result_score_range")
    private Integer result_score_range;

    /**
     * 该评分结果所属试卷id
     */
    @TableField(value = "test_paper_id")
    private Long test_paper_id;

    /**
     * 试卷创建人id
     */
    @TableField(value = "user_id")
    private Long user_id;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date create_time;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date update_time;

    /**
     * 逻辑删除、0表示未删除，1表示删除
     */
    @TableField(value = "is_delete")
    @TableLogic
    private Integer is_delete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}