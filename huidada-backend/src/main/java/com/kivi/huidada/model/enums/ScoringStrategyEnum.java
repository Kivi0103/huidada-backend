package com.kivi.huidada.model.enums;

public enum ScoringStrategyEnum {
    CUSTOM("自定义",0),
    AI("AI",1);
    private final String text;
    private final int value;


    ScoringStrategyEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public String gettext() {
        return text;
    }

    public int getValue() {
        return value;
    }
}
