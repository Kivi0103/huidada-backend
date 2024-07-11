package com.kivi.huidada.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kivi.huidada.common.ErrorCode;
import com.kivi.huidada.exception.BusinessException;
import com.kivi.huidada.model.entity.User;
import com.kivi.huidada.model.vo.UserVO;
import com.kivi.huidada.service.UserService;
import com.kivi.huidada.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author Kivi
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-07-03 14:49:05
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    /**
     * 密码盐值
     */
    private static final String SALT = "kivi";
    private String USER_LOGIN_STATE = "user_login";

    /**
     * 注册用户
     * @param userName
     * @param password
     * @param headPicture
     * @return
     */
    @Override
    public Boolean userRegister(String userName, String password, String passwordConfirm, String headPicture) {
        // 校验用户名长度
        if(userName.length() < 4 || userName.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度必须在4-20位之间");
        }
        // 校验密码是否一致
        if(!password.equals(passwordConfirm)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码输入不一致");
        }
        // 校验密码是否符合规范
        if(password.length() < 8 || password.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度必须在8-20位之间");
        }
        if(!password.matches("^(?=.*[A-Za-z])(?=.*\\d).+$")){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码必须包含大小写字母和数字");
        }
        // 加锁，防止多线程下用户信息重复
        synchronized (userName.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_name", userName);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已存在，请更换一个账号注册！");
            }
            // 加密密码
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
            // 向数据库添加用户信息
            User user = new User();
            user.setUserName(userName);
            user.setPassword(encryptPassword);
            user.setHeadPicture(headPicture);
            boolean save = this.save(user);
            if (!save) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库错误、注册失败");
            }
            return save;
        }
    }

    @Override
    public UserVO userLogin(String userName, String password, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userName, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userName.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        queryWrapper.eq("password", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    @Override
    public UserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO loginUserVO = new UserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取当前登录用户， 用于后端其他业务添加用户信息时查询
     *
     * @param request
     * @return
     */
    // todo 优化，从缓存中获取用户信息
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

}




