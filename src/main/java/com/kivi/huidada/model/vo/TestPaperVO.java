package com.kivi.huidada.model.vo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kivi.huidada.model.dto.test_paper.QuestionItem;
import com.kivi.huidada.model.entity.TestPaper;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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

    /**
     * 审核状态
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    private static final long serialVersionUID = 1L;

    public static TestPaperVO objToVo(TestPaper testPaper) {
        if (testPaper == null) {
            return null;
        }
        TestPaperVO vo = new TestPaperVO();
        vo.setId(testPaper.getId());
        vo.setTestName(testPaper.getTestName());
        vo.setDescription(testPaper.getDescription());
        // 将json字符串转成列表
        vo.setQuestionContent(JSONUtil.toList(testPaper.getQuestionContent(), QuestionItem.class));
        vo.setIsAi(testPaper.getIsAi());
        vo.setUserId(testPaper.getUserId());
        vo.setUserName(testPaper.getUserName());
        vo.setBgPicture(testPaper.getBgPicture());
        vo.setType(testPaper.getType());
        vo.setScoringStrategyType(testPaper.getScoringStrategyType());
        vo.setCreateTime(testPaper.getCreateTime());
        vo.setUpdateTime(testPaper.getUpdateTime());
        vo.setIsDelete(testPaper.getIsDelete());
        vo.setReviewStatus(testPaper.getReviewStatus());
        vo.setReviewMessage(testPaper.getReviewMessage());
        return vo;
    }
}