package com.kivi.huidada.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kivi.huidada.model.dto.test_paper.*;
import com.kivi.huidada.model.entity.TestPaper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kivi.huidada.model.vo.TestCountVO;
import com.kivi.huidada.model.vo.TestPaperVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Kivi
* @description 针对表【test_paper(试卷表)】的数据库操作Service
* @createDate 2024-07-03 14:49:05
*/
public interface TestPaperService extends IService<TestPaper> {

    QueryWrapper<TestPaper> getQueryWrapper(TestPaperQueryRequestDTO testPaperQueryRequestDTO);

    Page<TestPaperVO> getAppVOPage(Page<TestPaper> appPage, HttpServletRequest request);

    Long addTestPaper(TestPaperAddRequestDTO testPaperAddRequestDTO, HttpServletRequest request);

    List<QuestionItem> aiGenerateQuestion(AiGenerateQuestionRequestDTO aiGenerateQuestionRequestDTO, HttpServletRequest request);

    Boolean updateTestPaper(TestPaperUpdateRequestDTO testPaperUpdateRequestDTO, HttpServletRequest request);

    UpdateWrapper<TestPaper> getUpdateWrapper(TestPaperUpdateRequestDTO testPaperUpdateRequestDTO);

    Boolean delete(Long id);

    String validAndGetUserMessage(AiGenerateQuestionRequestDTO aiGenerateQuestionRequestDTO, HttpServletRequest request);

    SseEmitter aiGenerateQuestionSSE(AiGenerateQuestionRequestDTO aiGenerateQuestionRequestDTO, HttpServletRequest request);

    List<TestCountVO> testCountTop10();
}
