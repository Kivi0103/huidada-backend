package com.kivi.huidada.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kivi.huidada.common.BaseResponse;
import com.kivi.huidada.common.ErrorCode;
import com.kivi.huidada.common.ResultUtils;
import com.kivi.huidada.exception.BusinessException;
import com.kivi.huidada.exception.ThrowUtils;
import com.kivi.huidada.model.dto.test_paper.TestPaperAddRequestDTO;
import com.kivi.huidada.model.dto.test_paper.TestPaperQueryRequestDTO;
import com.kivi.huidada.model.entity.TestPaper;
import com.kivi.huidada.model.entity.User;
import com.kivi.huidada.model.enums.TestPaperReviewStatusEnum;
import com.kivi.huidada.model.vo.TestPaperVO;
import com.kivi.huidada.service.TestPaperService;
import com.kivi.huidada.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/testPaper")
@Api(tags = "TestPaperController")
@Slf4j
public class TestPaperController {

    @Resource
    private TestPaperService testPaperService;

    @Resource
    private UserService userService;

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
        // 只能看到已过审的应用
        testPaperQueryRequestDTO.setReviewStatus(TestPaperReviewStatusEnum.PASS.getValue());
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
    public BaseResponse<Long> addTestPaper(@RequestBody TestPaperAddRequestDTO testPaperAddRequestDTO, HttpServletRequest request) {
        if( testPaperAddRequestDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加测试参数错误");
        }
        return ResultUtils.success(testPaperService.addTestPaper(testPaperAddRequestDTO, request));
    }

    /**
     *
     * 获得测试总数
     * @param request
     * @return
     */
    @GetMapping("/getCount")
    public BaseResponse<Long> getCount(HttpServletRequest request) {
        return ResultUtils.success(testPaperService.count());
    }
}
