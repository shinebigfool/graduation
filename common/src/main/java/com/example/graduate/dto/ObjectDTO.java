package com.example.graduate.dto;

import com.example.graduate.codeEnum.RetCodeEnum;
import lombok.Data;

@Data
public class ObjectDTO extends DTO{
    private Object data;
    public ObjectDTO(){
        super();
    }
    public ObjectDTO(RetCodeEnum e){
        super(e);
    }
    public ObjectDTO(String code,String msg){
        super(code,msg);
    }
}
