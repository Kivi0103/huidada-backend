package com.kivi.huidada.controller;

import cn.hutool.json.JSONUtil;
import com.kivi.huidada.common.BaseResponse;
import com.kivi.huidada.common.ResultUtils;
import com.kivi.huidada.model.dto.user_answer.CommitUserChoiceRequestDTO;
import com.kivi.huidada.model.vo.TestResultVO;
import com.kivi.huidada.model.vo.UserAnswerVO;
import com.kivi.huidada.service.UserAnswerService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/userAnswer")
@Api(tags = "UserAnswerController")
@Slf4j
public class UserAnswerController {

    @Resource
    private UserAnswerService userAnswerService;
    @PostMapping("/submitCustomAnswer")
    public BaseResponse<TestResultVO> submitCustomAnswer(@RequestBody CommitUserChoiceRequestDTO answer, HttpServletRequest request) {
        log.info("Received answer: " + answer);
        TestResultVO testResultVO = userAnswerService.submitCustomAnswer(answer,request);
        return ResultUtils.success(testResultVO);
    }
}
