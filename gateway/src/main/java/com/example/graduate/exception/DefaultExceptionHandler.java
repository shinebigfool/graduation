package com.example.graduate.exception;

import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 倪鑫彦
 * @description shiro异常处理类
 * @since 10:47 2020/11/10
 */
@ControllerAdvice
public class DefaultExceptionHandler {
    /**
     *
     * 捕获异常，返回DTO
     * @param e 异常
     * @return com.example.graduate.dto.DTO
     */
    @ExceptionHandler({ UnauthorizedException.class, AuthorizationException.class })
    @ResponseBody
    public DTO authorizationException(Exception e) {
        return new DTO(RetCodeEnum.FORBIDDEN);
    }

}
