package com.example.graduate.dto;

import com.example.graduate.codeEnum.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "响应数据传输对象", description = "响应数据传输对象")
public class DTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "响应信息编码")
    private String retCode;

    @ApiModelProperty(value = "响应信息")
    private String retMsg;

    @ApiModelProperty(value = "TraceId")
    private String tid;

    public DTO() {
    }

    public DTO(RetCodeEnum e) {
        this.retCode = e.getCode();
        this.retMsg = e.getTip();
    }
    public DTO(String retCode, String retMsg) {
        this.retCode = retCode;
        this.retMsg = retMsg;
    }
    @ApiModelProperty(value = "响应异常信息", hidden = true)
    public void setRetException(String retMsg) {
//        this.setRetCode(RetCodeEnum.EXCEPTION);
        this.setRetMsg(retMsg);
    }

    @ApiModelProperty(value = "响应指定信息", hidden = true)
    public void setResult(RetCodeEnum e) {
        this.retCode = e.getCode();
        this.retMsg = e.getTip();
    }
}
