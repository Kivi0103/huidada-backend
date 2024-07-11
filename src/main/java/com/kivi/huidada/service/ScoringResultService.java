package com.kivi.huidada.service;

import com.kivi.huidada.model.dto.scoring_result.ScoringResultAddRequestDTO;
import com.kivi.huidada.model.entity.ScoringResult;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author Kivi
* @description 针对表【scoring_result(评分结果表)】的数据库操作Service
* @createDate 2024-07-03 14:49:05
*/
public interface ScoringResultService extends IService<ScoringResult> {

    Boolean add(ScoringResultAddRequestDTO scoringResultAddRequestDTO, HttpServletRequest request);
}
