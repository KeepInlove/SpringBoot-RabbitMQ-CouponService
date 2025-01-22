package com.example.springbootmybatisplus.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-15 16:08
 * @email gxy06x@qq.com
 */
@TableName("tb_user")
@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String userName;
    private String phone;
    private String password;
    private String salt;
    private String head;
    private Integer loginCount;
    private Date registerDate;
}
