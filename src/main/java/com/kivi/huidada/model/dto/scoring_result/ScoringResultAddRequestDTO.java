package com.kivi.huidada.model.dto.scoring_result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ScoringResultAddRequestDTO implements Serializable {

    /**
     * 结果名称
     */
    private String result_name;

    /**
     * 结果描述
     */
    private String result_desc;

    /**
     * 结果属性集合 JSON，如 [I,S,T,J]，用于测评类试卷的匹配
     */
    private List<String> result_prop;

    /**
     * 结果图片、创建用户上传、存在cos中的地址
     */
    private String result_picture;

    /**
     * 结果得分范围，用于打分类试卷匹配结果，如 80，表示 80及以上的分数命中此结果
     */
    private Integer result_score_range;

    /**
     * 该评分结果所属试卷id
     */
    private Long test_paper_id;

    private static final long serialVersionUID = 1L;
}
