package com.kivi.huidada.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class TestCountVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private String testName;
    private int testCount;
}
