package com.kivi.huidada.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kivi.huidada.model.dto.test_paper.QuestionItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionContentVO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long testPaperId;
    private List<QuestionItem> questionContent;
    private static final long serialVersionUID = 1L;
}
