package com.kivi.huidada.service;

import com.kivi.huidada.model.dto.user_answer.CommitUserChoiceRequestDTO;
import com.kivi.huidada.model.entity.UserAnswer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kivi.huidada.model.vo.TestResultVO;
import com.kivi.huidada.model.vo.UserAnswerVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Kivi
* @description 针对表【user_answer(用户答案表)】的数据库操作Service
* @createDate 2024-07-03 14:49:05
*/
public interface UserAnswerService extends IService<UserAnswer> {

    TestResultVO submitCustomAnswer(CommitUserChoiceRequestDTO answer, HttpServletRequest request);
}
