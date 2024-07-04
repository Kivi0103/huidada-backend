package com.kivi.huidada.model.dto.scoring_result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ScoringResultUpdateRequestDTO implements Serializable {
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
    private List<String> resultProp;

    /**
     * 结果图片、创建用户上传、存在cos中的地址
     */
    private String resultPicture;

    /**
     * 结果得分范围，用于打分类试卷匹配结果，如 80，表示 80及以上的分数命中此结果
     */
    private Integer resultScoreRange;

    private static final long serialVersionUID = 1L;
}
