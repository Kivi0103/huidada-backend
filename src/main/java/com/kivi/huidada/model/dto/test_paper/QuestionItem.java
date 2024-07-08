package com.kivi.huidada.model.dto.test_paper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class QuestionItem {

    private String questionDesc;

    private List<option> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class option{
        private String result;
        private int score;
        private String optionDesc;
        private String key;
    }
}
