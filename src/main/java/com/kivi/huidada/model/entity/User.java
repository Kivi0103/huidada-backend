package com.kivi.huidada.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "user_name")
    private String user_name;

    /**
     * 用户密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 用户头像，关联到cos存储地址
     */
    @TableField(value = "head_picture")
    private String head_picture;

    /**
     * 用户权限、0为普通用户，1为管理员，-1为禁止人员
     */
    @TableField(value = "role")
    private Integer role;

    /**
     * 微信开放平台id
     */
    @TableField(value = "union_id")
    private String union_id;

    /**
     * 公众号openId
     */
    @TableField(value = "mp_open_id")
    private String mp_open_id;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date create_time;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date update_time;

    /**
     * 逻辑删除、0表示未删除，1表示删除
     */
    @TableField(value = "is_delete")
    @TableLogic
    private Integer is_delete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}