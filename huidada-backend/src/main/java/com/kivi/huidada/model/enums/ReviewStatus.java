package com.kivi.huidada.model.enums;

/**
 * 审核状态枚举类
 */
public enum ReviewStatus {
    REVIEWING("待审核",0),
    PASS("通过",1),
    REJECT("拒绝",2);
    private final String text;
    private final int value;


    ReviewStatus(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public int getValue() {
        return value;
    }
}
