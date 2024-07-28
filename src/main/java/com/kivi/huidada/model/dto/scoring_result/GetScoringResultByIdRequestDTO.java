package com.kivi.huidada.model.dto.scoring_result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetScoringResultByIdRequestDTO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long testId;
}
