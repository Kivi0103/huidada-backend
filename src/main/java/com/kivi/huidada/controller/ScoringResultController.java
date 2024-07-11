package com.kivi.huidada.controller;

import co.elastic.clients.elasticsearch.nodes.Http;
import com.kivi.huidada.common.BaseResponse;
import com.kivi.huidada.common.ErrorCode;
import com.kivi.huidada.common.ResultUtils;
import com.kivi.huidada.exception.BusinessException;
import com.kivi.huidada.model.dto.scoring_result.ScoringResultAddRequestDTO;
import com.kivi.huidada.service.ScoringResultService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/scoringResult")
@Api(tags = "ScoringResultController")
@Slf4j
public class ScoringResultController {
    @Resource
    private ScoringResultService scoringResultService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addScoringResult(@RequestBody ScoringResultAddRequestDTO scoringResultAddRequestDTO, HttpServletRequest request) {
        if( scoringResultAddRequestDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "结果参数错误");
        }
        return ResultUtils.success(scoringResultService.add(scoringResultAddRequestDTO, request));

    }
}
