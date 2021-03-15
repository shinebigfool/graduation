package com.example.graduate.dto;

import com.example.graduate.codeEnum.RetCodeEnum;
import lombok.Data;

import java.util.List;

@Data
public class RoleDTO extends DTO{
    private static final long serialVersionUID = 1L;
    //角色列表
    private List<String> roles;
    //头像地址
    private String avatar;
    //用户名
    private String name;
    private Integer mainRole;
    public RoleDTO(){
        super();
    }
    public RoleDTO(RetCodeEnum e){
        super(e);
    }
    public RoleDTO(String retCode,String msg){
        super(retCode,msg);
    }
}
