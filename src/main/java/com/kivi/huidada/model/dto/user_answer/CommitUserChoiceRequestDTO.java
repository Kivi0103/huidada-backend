package com.kivi.huidada.model.dto.user_answer;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

@Data
public class CommitUserChoiceRequestDTO {
    /**
     * 所属试卷id
     */
    private Long testPaperId;

    /**
     * 用户答案（JSON 数组：[A,B,A,C...]）
     */
    private List<String> choices;

    /**
     * 试卷类型，0表示打分类，1表示测评类
     */
    private Integer type;

    /**
     * 评分策略类型，0表示用户自定义的评分策略，1表示ai生成的评分策略
     */
    private Integer scoringStrategyType;

}
