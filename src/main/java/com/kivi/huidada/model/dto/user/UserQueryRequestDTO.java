package com.kivi.huidada.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryRequestDTO implements Serializable {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String user_name;

    private static final long serialVersionUID = 1L;

}
