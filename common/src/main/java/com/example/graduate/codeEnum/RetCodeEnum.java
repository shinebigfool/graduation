package com.example.graduate.codeEnum;

public enum RetCodeEnum {
    SUCCEED("000000", "成功"),
    /**
     *系统级响应99开头，6位
     */
    FAIL("999999", "操作失败"),

    EXCEPTION("999998", "系统异常"),

    SYS_BUSY("990001", "系统正忙,请稍后再试"),

    TIME_OUT("990002", "请求超时"),

    /**
     *业务级响应90开头，6位
     */
    RESULT_EMPTY("900001", "结果为空"),

    PARAM_LACK("900002", "缺少参数"),

    PARAM_ERROR("900003", "参数错误"),

    FORBIDDEN("900004", "没有权限"),

    PSW_EMPTY("900005", "密码为空"),

    AUDIT_FLAG("900006", "该操作已提交至审核记录中,请等待审核"),

    AUDIT_OBJECT_EXIST("900007", "对象已存在,请勿重复添加!"),

    AUDIT_OBJECT_DELETED("900008", "对象不存在!"),

    AUDIT_DEV_IP_EXIST("900009", "该设备所设置的IP地址已经存在,不能重复"),

    AUDIT_OBJECT_CONSTRAINT_OK("900010", "审核对象合法"),
    PSW_WRONG("900011","密码错误");

    private String code;
    private String tip;

    private RetCodeEnum(String code, String tip) {
        this.code = code;
        this.tip = tip;
    }

    public String getCode() {
        return this.code;
    }

    public String getTip() {
        return this.tip;
    }

}
