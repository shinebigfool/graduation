package com.example.graduate.dto;


import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.pojo.AdminRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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

    private boolean enabled;
    private int age;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private int sex;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registTime;
    private int classId;
    private List<Integer> roles;
    private List<AdminRole> adminRoles;
    private String photoUrl;
    //1学生2老师3家长4访客
    private int mainRole;
    private String className;
    public UserDTO(RetCodeEnum resultEmpty) {
        super(resultEmpty);
    }
    public UserDTO(){
        super();
    }
}
