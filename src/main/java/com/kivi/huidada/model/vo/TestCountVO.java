package com.kivi.huidada.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class TestCountVO {
    
    private Long id;
    private String testName;
    private int testCount;
}
