package com.kivi.huidada.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kivi.huidada.common.ErrorCode;
import com.kivi.huidada.model.dto.test_paper.TestPaperAddRequestDTO;
import com.kivi.huidada.model.dto.test_paper.TestPaperQueryRequestDTO;
import com.kivi.huidada.model.entity.TestPaper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kivi.huidada.model.vo.TestPaperVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Kivi
* @description 针对表【test_paper(试卷表)】的数据库操作Service
* @createDate 2024-07-03 14:49:05
*/
public interface TestPaperService extends IService<TestPaper> {

    QueryWrapper<TestPaper> getQueryWrapper(TestPaperQueryRequestDTO testPaperQueryRequestDTO);

    Page<TestPaperVO> getAppVOPage(Page<TestPaper> appPage, HttpServletRequest request);

    Long addTestPaper(TestPaperAddRequestDTO testPaperAddRequestDTO, HttpServletRequest request);
}
