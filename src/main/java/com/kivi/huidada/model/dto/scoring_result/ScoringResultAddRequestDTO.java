package com.kivi.huidada.model.dto.scoring_result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ScoringResultAddRequestDTO implements Serializable {
    /**
     * 评分结果列表
     */
    private List<ScoringResultItem> scoringResults;

    /**
     * 该评分结果所属试卷id
     */
    private Long testPaperId;

    private static final long serialVersionUID = 1L;
}
