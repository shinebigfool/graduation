package com.example.graduate.exception.handler;

import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ParameterValidExceptionHandle {
    /**
     *
     * @param exception JSON方式提交（Content-Type: application/json）时，参数校验失败抛出的异常
     * @return com.example.graduate.dto.DTO
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DTO methodException(MethodArgumentNotValidException exception) {
        if(exception.getBindingResult().getFieldError()==null){
            return null;
        }
        return new DTO(RetCodeEnum.FAIL.getCode(),exception.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     *
     * @param exception FORM方式提交时，参数校验失败抛出的异常
     * @return com.example.graduate.dto.DTO
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public DTO constraintViolationException(ConstraintViolationException exception){
        if(CollectionUtils.isEmpty(exception.getConstraintViolations())){
            return null;
        }
        return new DTO(RetCodeEnum.FAIL.getCode(),exception.getConstraintViolations().iterator().next().getMessage());
    }
}
