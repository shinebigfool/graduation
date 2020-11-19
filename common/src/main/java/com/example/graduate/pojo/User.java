package com.example.graduate.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class User {
    @TableId(type = IdType.AUTO)
    private int id;

    @NotBlank(message = "用户账号不能为空")
    @ApiModelProperty(value = "用户登录名")
    private String name;

    @ApiModelProperty(value = "密码")
    private String password;

    private String salt;

    private String uname;

    private String phone;

    private String email;

    private boolean enabled;

    private int sex;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registTime;
    private String photoUrl;
//
//    @Transient
//    private List<AdminRole> roles;
}
