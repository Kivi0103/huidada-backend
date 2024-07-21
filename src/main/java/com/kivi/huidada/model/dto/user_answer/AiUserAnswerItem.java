package com.kivi.huidada.model.dto.user_answer;

import lombok.Data;

@Data
public class AiUserAnswerItem {
    /**
     * 题目描述
     */
    private String question;

    /**
     * 用户选择的答案描述
     */
    private String answer;
}
