package com.example.graduate.exception;

import com.example.graduate.codeEnum.RetCodeEnum;

public class NxyException extends Exception{
    private String code;
    private String message;
    public NxyException(RetCodeEnum codeEnum,String message){
        super(message);
        this.code = codeEnum.getCode();
        this.message = message;
    }
    public NxyException(String message){
        this(RetCodeEnum.EXCEPTION,message);
    }
    public String getCode(){
        return code;
    }
    public String getMessage(){
        return message;
    }
}
