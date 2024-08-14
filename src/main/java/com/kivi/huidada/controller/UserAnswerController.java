package com.kivi.huidada.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kivi.huidada.common.BaseResponse;
import com.kivi.huidada.common.ErrorCode;
import com.kivi.huidada.common.ResultUtils;
import com.kivi.huidada.exception.ThrowUtils;
import com.kivi.huidada.model.dto.test_paper.TestPaperQueryRequestDTO;
import com.kivi.huidada.model.dto.user_answer.CommitUserChoiceRequestDTO;
import com.kivi.huidada.model.dto.user_answer.UserAnswerQueryRequestDTO;
import com.kivi.huidada.model.entity.TestPaper;
import com.kivi.huidada.model.entity.User;
import com.kivi.huidada.model.entity.UserAnswer;
import com.kivi.huidada.model.vo.AppAnswerResultCountVO;
import com.kivi.huidada.model.vo.TestPaperVO;
import com.kivi.huidada.model.vo.TestResultVO;
import com.kivi.huidada.model.vo.UserAnswerVO;
import com.kivi.huidada.service.UserAnswerService;
import com.kivi.huidada.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @Resource
    private UserService userService;

    @PostMapping("/submitCustomAnswer")
    public BaseResponse<TestResultVO> submitCustomAnswer(@RequestBody CommitUserChoiceRequestDTO answer, HttpServletRequest request) throws Exception {
        log.info("Received answer: " + answer);
        TestResultVO testResultVO = userAnswerService.submitCustomAnswer(answer,request);
        return ResultUtils.success(testResultVO);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserAnswerVO>> listUserAnswerVOByPage(@RequestBody UserAnswerQueryRequestDTO userAnswerQueryRequestDTO,
                                                           HttpServletRequest request) {
        long current = userAnswerQueryRequestDTO.getCurrent();
        long size = userAnswerQueryRequestDTO.getPageSize();
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<UserAnswer> userAnswerPage =userAnswerService.page(new Page<>(current, size),
                userAnswerService.getQueryWrapper(userAnswerQueryRequestDTO));
        // 获取封装类
        return ResultUtils.success(userAnswerService.getUserAnswerVOPage(userAnswerPage, request));
    }

    @PostMapping("/getUserAnswerCount")
    public BaseResponse<Long> getUserAnswerCount(@RequestBody UserAnswerQueryRequestDTO userAnswerQueryRequestDTO) {
        return ResultUtils.success(userAnswerService.count(userAnswerService.getQueryWrapper(userAnswerQueryRequestDTO)));
    }

    @GetMapping("/generateId")
    public BaseResponse<Long> generateUserAnswerId() {
        return ResultUtils.success(IdUtil.getSnowflakeNextId());
    }

    @GetMapping("/userAnswerCuntByTestPaperId")
    public BaseResponse<List<AppAnswerResultCountVO>> userAnswerCuntByTestPaperId(@RequestParam("testPaperId") Long testPaperId){
        return ResultUtils.success(userAnswerService.userAnswerCuntByTestPaperId(testPaperId));
    }
}
