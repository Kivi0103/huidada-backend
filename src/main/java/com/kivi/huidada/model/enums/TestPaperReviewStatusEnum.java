package com.kivi.huidada.model.enums;

import cn.hutool.core.util.ObjectUtil;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TestPaperReviewStatusEnum {
    /**
     * 审核状态枚举类：0-待审核, 1-通过, 2-拒绝
     */

    REVIEWING("待审核", 0),
    PASS("通过", 1),
    REJECT("拒绝", 2);

    private final String text;

    private final int value;

    TestPaperReviewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static TestPaperReviewStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (TestPaperReviewStatusEnum anEnum : TestPaperReviewStatusEnum.values()) {
            if (anEnum.value == value) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
