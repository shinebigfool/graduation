package com.example.graduate.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Data
//@TableName("user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
//
//    @Transient
//    private List<AdminRole> roles;
}
