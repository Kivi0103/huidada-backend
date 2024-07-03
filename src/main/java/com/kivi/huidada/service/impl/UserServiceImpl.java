package com.kivi.huidada.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kivi.huidada.model.entity.User;
import com.kivi.huidada.service.UserService;
import com.kivi.huidada.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Kivi
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-07-03 14:49:05
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




