package com.kivi.huidada.controller;

import com.kivi.huidada.common.BaseResponse;
import com.kivi.huidada.common.ErrorCode;
import com.kivi.huidada.common.ResultUtils;
import com.kivi.huidada.exception.BusinessException;
import com.kivi.huidada.exception.ThrowUtils;
import com.kivi.huidada.model.dto.user.UserAddRequestDTO;
import com.kivi.huidada.model.dto.user.UserLoginRequestDTO;
import com.kivi.huidada.model.entity.User;
import com.kivi.huidada.model.vo.UserVO;
import com.kivi.huidada.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@Api(tags = "User")
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    @ApiOperation("用户注册接口")
    public BaseResponse<Boolean> register(@RequestBody UserAddRequestDTO userAddRequestDTO) {
        if(userAddRequestDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户参数错误");
        }
        String userName = userAddRequestDTO.getUserName();
        String password = userAddRequestDTO.getPassword();
        String passwordConfirm = userAddRequestDTO.getPasswordConfirm();
        String headPicture = userAddRequestDTO.getHeadPicture();
        if(userName == null || password == null){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码不能为空");
        }
        Boolean is_add = userService.register(userName, password, passwordConfirm, headPicture);
        return ResultUtils.success(true);
    }

    /**
     * 用户登录接口
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequestDTO userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userName = userLoginRequest.getUserName();
        String password = userLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(userName, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO userVO = userService.userLogin(userName, password, request);
        return ResultUtils.success(userVO);
    }

}
