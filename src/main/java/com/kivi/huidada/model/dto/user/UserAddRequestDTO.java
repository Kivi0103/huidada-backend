package com.kivi.huidada.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAddRequestDTO implements Serializable {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 二次密码
     */
    private String passwordConfirm;

    /**
     * 用户头像，关联到cos存储地址
     */
    private String headPicture;

    private static final long serialVersionUID = 3191241716373120793L;

}
