package com.kivi.huidada.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.kivi.huidada.model.dto.test_paper.QuestionItem;
import com.kivi.huidada.model.entity.TestPaper;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 试卷表
 * @TableName test_paper
 */
@Data
public class TestPaperVO implements Serializable {
    /**
     * 试卷id
     */
    private Long id;

    /**
     * 试卷名称
     */
    private String testName;

    /**
     * 试卷描述
     */
    private String description;

    /**
     * 题目内容，每道题由题目、选项key，选项值构成
     */
    private List<QuestionItem> questionContent;

    /**
     * 是否为ai生成试卷题目，0表示自定义的试卷题目，1表示ai试卷
     */
    private Integer isAi;

    /**
     * 试卷创建人id
     */
    private Long userId;

    /**
     * 试卷创建人名称
     */
    private String userName;

    /**
     * 试卷封面背景图，关联到cos存储地址
     */
    private String bgPicture;

    /**
     * 试卷类型，0表示打分类，1表示测评类
     */
    private Integer type;

    /**
     * 评分策略类型，0表示用户自定义的评分策略，1表示ai生成的评分策略
     */
    private Integer scoringStrategyType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除、0表示未删除，1表示删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    public static TestPaperVO objToVo(TestPaper testPaper) {
        if (testPaper == null) {
            return null;
        }
        TestPaperVO vo = new TestPaperVO();
        BeanUtil.copyProperties(testPaper, vo);
        return vo;
    }
}