package com.kivi.huidada.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评分结果表
 * @TableName scoring_result
 */
@Data
public class ScoringResultVO implements Serializable {
    /**
     * 评分策略id
     */
    
    private Long id;

    /**
     * 结果名称
     */
    private String resultName;

    /**
     * 结果描述
     */
    private String resultDesc;

    /**
     * 结果属性集合 JSON，如 [I,S,T,J]，用于测评类试卷的匹配
     */
    private String resultProp;

    /**
     * 结果图片、创建用户上传、存在cos中的地址
     */
    private String resultPicture;

    /**
     * 结果得分范围，用于打分类试卷匹配结果，如 80，表示 80及以上的分数命中此结果
     */
    private Integer resultScoreRange;

    /**
     * 该评分结果所属试卷id
     */
    private Long testPaperId;

    /**
     * 试卷创建人id
     */
    
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}