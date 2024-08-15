package com.kivi.huidada.model.dto.test_paper;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetTestPaperByIdDTO implements Serializable {
    
    private Long id;

    private static final long serialVersionUID = 1L;
}
