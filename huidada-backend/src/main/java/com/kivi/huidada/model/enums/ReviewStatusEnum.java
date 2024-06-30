package com.kivi.huidada.model.enums;

import cn.hutool.core.util.ObjectUtil;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审核状态枚举类
 */
public enum ReviewStatusEnum {
    REVIEWING("待审核",0),
    PASS("通过",1),
    REJECT("拒绝",2);
    private final String text;
    private final int value;


    ReviewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public static ReviewStatusEnum getEnumByValue(int value) {
        if(ObjectUtil.isEmpty(value))return null;
        for(ReviewStatusEnum e : ReviewStatusEnum.values()){
            if(e.getValue() == value)return e;
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

    public String getText() {
        return text;
    }

    public int getValue() {
        return value;
    }
}
