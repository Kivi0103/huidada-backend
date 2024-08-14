package com.kivi.huidada.mapper;

import com.kivi.huidada.model.entity.UserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kivi.huidada.model.vo.AppAnswerResultCountVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author Kivi
* @description 针对表【user_answer(用户答案表)】的数据库操作Mapper
* @createDate 2024-07-03 14:49:05
* @Entity com.kivi.huidada.model.entity.UserAnswer
*/
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {

    List<AppAnswerResultCountVO> userAnswerCuntByTestPaperId(Long testPaperId);
}




