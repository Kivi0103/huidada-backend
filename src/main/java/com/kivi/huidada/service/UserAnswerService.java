package com.kivi.huidada.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kivi.huidada.model.dto.user_answer.CommitUserChoiceRequestDTO;
import com.kivi.huidada.model.dto.user_answer.UserAnswerQueryRequestDTO;
import com.kivi.huidada.model.entity.UserAnswer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kivi.huidada.model.vo.AppAnswerResultCountVO;
import com.kivi.huidada.model.vo.TestResultVO;
import com.kivi.huidada.model.vo.UserAnswerVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Kivi
* @description 针对表【user_answer(用户答案表)】的数据库操作Service
* @createDate 2024-07-03 14:49:05
*/
public interface UserAnswerService extends IService<UserAnswer> {

    TestResultVO submitCustomAnswer(CommitUserChoiceRequestDTO answer, HttpServletRequest request) throws Exception;

    QueryWrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequestDTO userAnswerQueryRequestDTO);

    Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> userAnswerPage, HttpServletRequest request);

    List<AppAnswerResultCountVO> userAnswerCuntByTestPaperId(Long testPaperId);
}
