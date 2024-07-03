package com.kivi.huidada.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kivi.huidada.model.entity.UserAnswer;
import com.kivi.huidada.service.UserAnswerService;
import com.kivi.huidada.mapper.UserAnswerMapper;
import org.springframework.stereotype.Service;

/**
* @author Kivi
* @description 针对表【user_answer(用户答案表)】的数据库操作Service实现
* @createDate 2024-07-03 14:49:05
*/
@Service
public class UserAnswerServiceImpl extends ServiceImpl<UserAnswerMapper, UserAnswer>
    implements UserAnswerService {

}




