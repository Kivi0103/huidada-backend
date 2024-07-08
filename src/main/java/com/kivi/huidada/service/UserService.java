package com.kivi.huidada.service;

import com.kivi.huidada.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kivi.huidada.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Kivi
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-07-03 14:49:05
*/
public interface UserService extends IService<User> {

    Boolean userRegister(String userName, String password, String passwordConfirm, String email);

    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    public UserVO getLoginUserVO(User user);

    User getLoginUser(HttpServletRequest request);

    boolean userLogout(HttpServletRequest request);
}
