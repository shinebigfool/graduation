package com.example.graduate.codeEnum;

public enum LogOperationType {
    ADD("新增",1),
    UPDATE("修改",11),
    DELETE("删除",3),
    IMPORT("导入",4),
    SEND("消息发送",5),
    LOGIN("登录",6),
    EXAMINE("审核",2);

    private String type;
    private int code;
    LogOperationType(String type,int code){
        this.type = type;
        this.code = code;
    }
    public String getType(){
        return type;
    }
    public int getCode(){
        return code;
    }
}
