package com.kivi.huidada.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Question;
import com.kivi.huidada.service.QuestionService;
import com.kivi.huidada.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author Kivi
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2024-06-28 22:05:48
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}




