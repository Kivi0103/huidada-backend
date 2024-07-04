package com.kivi.huidada.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequestDTO implements Serializable {
    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码
     */
    private String password;

    private static final long serialVersionUID = 3191241716373120793L;
}
