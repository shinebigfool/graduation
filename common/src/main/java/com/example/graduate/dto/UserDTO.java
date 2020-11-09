package com.example.graduate.dto;


import com.example.graduate.codeEnum.RetCodeEnum;
import lombok.Data;

@Data
public class UserDTO extends DTO{
    private static final long serialVersionUID = 1L;
    private String name;
    private String password;

    private String salt;

    private String uname;

    private String phone;

    private String email;

    public UserDTO(RetCodeEnum resultEmpty) {
        super(resultEmpty);
    }
    public UserDTO(){
        super();
    }
}
