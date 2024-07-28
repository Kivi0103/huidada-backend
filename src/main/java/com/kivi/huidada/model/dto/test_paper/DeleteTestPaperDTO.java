package com.kivi.huidada.model.dto.test_paper;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteTestPaperDTO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
}
