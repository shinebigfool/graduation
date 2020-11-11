package com.example.graduate.dto;


import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.pojo.AdminRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDTO extends DTO{
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String password;

    private String salt;

    private String uname;

    private String phone;

    private String email;

    private boolean enable;

    private int sex;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registTime;
    private List<AdminRole> roles;
    public UserDTO(RetCodeEnum resultEmpty) {
        super(resultEmpty);
    }
    public UserDTO(){
        super();
    }
}
