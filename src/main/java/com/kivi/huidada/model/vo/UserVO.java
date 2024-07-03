package com.kivi.huidada.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * @TableName user
 */
@Data
public class UserVO implements Serializable {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String user_name;


    /**
     * 用户头像，关联到cos存储地址
     */
    private String head_picture;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 更新时间
     */
    private Date update_time;


    private static final long serialVersionUID = 1L;
}