package com.example.graduate.exception.handler;

import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.exception.NxyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class RuntimeExceptionHandle {
    /**
     *
     * @param exception 运行时异常
     * @return com.example.graduate.dto.DTO
     */
    @ExceptionHandler(RuntimeException.class)
    public DTO runtimeException(RuntimeException exception){
        log.warn("--------运行时异常 起始--------",exception);
        log.warn("--------运行时异常 结束--------");
        return new DTO(RetCodeEnum.FAIL.getCode(),exception.getMessage());
    }
    /**
     *
     * @param exception 自定义异常被抛出
     * @return com.example.graduate.dto.DTO
     */
    @ExceptionHandler(NxyException.class)
    public DTO PersonalDefineException(NxyException exception){
        log.warn("--------运行时异常 起始--------",exception);
        log.warn("--------运行时异常 结束--------");
        return new DTO(RetCodeEnum.FAIL.getCode(),exception.getMessage());
    }


    /**
     *
     * @param e shiro登录，鉴权抛出异常
     * @return com.example.graduate.dto.DTO
     */
    @ExceptionHandler({ UnauthorizedException.class, AuthorizationException.class })
    @ResponseBody
    public DTO authorizationException(Exception e) {
        return new DTO(RetCodeEnum.FORBIDDEN);
    }
}
