package com.kivi.huidada.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TestResultVO implements Serializable {

    /**
     * 测试得分, 只有打分类测试才有得分
     */
    private Integer score;

    /**
     * 测试结果名称
     */
    private String resultName;

    /**
     * 测试结果描述
     */
    private String resultDesc;

    /**
     * 测试结果图片地址
     *
     */
    private String resultPicture;

    /**
     * 答案生成时间
     */
    private Date createTime;
}
