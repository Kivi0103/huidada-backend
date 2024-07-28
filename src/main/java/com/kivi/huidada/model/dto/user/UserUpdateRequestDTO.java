package com.kivi.huidada.model.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateRequestDTO implements Serializable {
    /**
     * 用户id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户原密码
     */
    private String oldPassword;

    /**
     * 用户新密码
     */
    private String newPassword;

    /**
     * 用户头像，关联到cos存储地址
     */
    private String headPicture;

    private static final long serialVersionUID = 1L;
}
