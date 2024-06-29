package com.kivi.huidada.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.UserAnswer;
import com.kivi.huidada.service.UserAnswerService;
import com.kivi.huidada.mapper.UserAnswerMapper;
import org.springframework.stereotype.Service;

/**
* @author Kivi
* @description 针对表【user_answer(用户答题记录)】的数据库操作Service实现
* @createDate 2024-06-28 22:05:48
*/
@Service
public class UserAnswerServiceImpl extends ServiceImpl<UserAnswerMapper, UserAnswer>
    implements UserAnswerService{

}




