package com.example.graduate.dto;

import com.example.graduate.codeEnum.RetCodeEnum;
import lombok.Data;

import java.util.List;

@Data
public class RoleDTO extends DTO{
    private static final long serialVersionUID = 1L;
    private List<String> roles;
    private String avatar;
    private String name;
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
