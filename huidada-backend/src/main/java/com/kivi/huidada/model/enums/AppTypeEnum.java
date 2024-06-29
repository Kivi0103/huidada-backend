package com.kivi.huidada.model.enums;

public enum AppTypeEnum {
    SCORE("得分类", 0),
    TEST("测评类",1);

    private final String text;
    private final int value;

    AppTypeEnum(String text, int value) {
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
