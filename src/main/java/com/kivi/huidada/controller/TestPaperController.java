package com.kivi.huidada.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kivi.huidada.common.BaseResponse;
import com.kivi.huidada.common.ErrorCode;
import com.kivi.huidada.common.ResultUtils;
import com.kivi.huidada.exception.BusinessException;
import com.kivi.huidada.exception.ThrowUtils;
import com.kivi.huidada.manager.ZhiPuAiManager;
import com.kivi.huidada.model.dto.test_paper.*;
import com.kivi.huidada.model.entity.TestPaper;
import com.kivi.huidada.model.entity.User;
import com.kivi.huidada.model.enums.TestPaperReviewStatusEnum;
import com.kivi.huidada.model.vo.QuestionContentVO;
import com.kivi.huidada.model.vo.TestPaperVO;
import com.kivi.huidada.service.TestPaperService;
import com.kivi.huidada.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/testPaper")
@Api(tags = "TestPaperController")
@Slf4j
public class TestPaperController {

    @Resource
    private TestPaperService testPaperService;


    /**
     * 分页查询测试
     * @param testPaperQueryRequestDTO
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<TestPaperVO>> listAppVOByPage(@RequestBody TestPaperQueryRequestDTO testPaperQueryRequestDTO,
                                                           HttpServletRequest request) {
        long current = testPaperQueryRequestDTO.getCurrent();
        long size = testPaperQueryRequestDTO.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<TestPaper> appPage =testPaperService.page(new Page<>(current, size),
                testPaperService.getQueryWrapper(testPaperQueryRequestDTO));
        // 获取封装类
        return ResultUtils.success(testPaperService.getAppVOPage(appPage, request));
    }

    /**
     *
     * 添加测试
     * @param testPaperAddRequestDTO
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<String> addTestPaper(@RequestBody TestPaperAddRequestDTO testPaperAddRequestDTO, HttpServletRequest request) {
        if( testPaperAddRequestDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加测试参数错误");
        }
        return ResultUtils.success(testPaperService.addTestPaper(testPaperAddRequestDTO, request).toString());
    }

    /**
     *
     * 获得测试总数
     * @param request
     * @return
     */
    @PostMapping("/getTestPaperCount")
    public BaseResponse<Long> getTestPaperCount(@RequestBody TestPaperQueryRequestDTO testPaperQueryRequestDTO, HttpServletRequest request) {
        if( testPaperQueryRequestDTO == null){
            return ResultUtils.success(testPaperService.count());
        }
        return ResultUtils.success(testPaperService.count(testPaperService.getQueryWrapper(testPaperQueryRequestDTO)));
    }

    /**
     * ai生成题目请求
     */
    @PostMapping("/aiGenerateQuestion")
    public BaseResponse<QuestionContentVO> aiGenerateQuestion(@RequestBody AiGenerateQuestionRequestDTO aiGenerateQuestionRequestDTO, HttpServletRequest request) {
        if (aiGenerateQuestionRequestDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "ai生成题目参数错误");
        }
        List<QuestionItem> questions = testPaperService.aiGenerateQuestion(aiGenerateQuestionRequestDTO, request);
        QuestionContentVO questionContentVO = new QuestionContentVO();
        questionContentVO.setQuestionContent(questions);
        return ResultUtils.success(questionContentVO);
    }

    /**
     * 更改测试
     * @param testPaperUpdateRequestDTO
     * @param request
     * @return
     */
    @PostMapping("/updateTestPaper")
    public BaseResponse<Boolean> updateTestPaper(@RequestBody TestPaperUpdateRequestDTO testPaperUpdateRequestDTO, HttpServletRequest request) {
        if( testPaperUpdateRequestDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新测试参数错误");
        }
        return ResultUtils.success(testPaperService.updateTestPaper(testPaperUpdateRequestDTO, request));
    }

    @PostMapping("/getTestPaperById")
    public BaseResponse<TestPaperVO> getTestPaperById(@RequestBody GetTestPaperByIdDTO getTestPaperByIdDTO, HttpServletRequest request) {
        Long id = getTestPaperByIdDTO.getId();
        if( id == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "获取测试参数错误");
        }
        TestPaper testPaper = testPaperService.getById(id);
        if(testPaper == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "测试不存在");
        }
        return ResultUtils.success(TestPaperVO.objToVo(testPaper));
    }

    @PostMapping("/deleteTestPaper")
    public BaseResponse<Boolean> deleteTestPaper(@RequestBody DeleteTestPaperDTO deleteTestPaperDTO, HttpServletRequest request) {
        Long id = deleteTestPaperDTO.getId();
        if( id == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除测试参数错误");
        }
        return ResultUtils.success(testPaperService.delete(id));
    }
}
