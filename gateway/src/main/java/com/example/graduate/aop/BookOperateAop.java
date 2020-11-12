package com.example.graduate.aop;

import com.example.graduate.annotation.OperationLogAnnotation;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BookOperationLog;
import com.example.graduate.service.BookOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * @author 倪鑫彦
 * @description 记录图书增删，审核日志
 * @since 15:17 2020/11/12
 */
@Aspect
@Component
@Slf4j
public class BookOperateAop {
    @Autowired
    private BookOperationLogService bookOperationLogService;

    @Pointcut("@annotation(com.example.graduate.annotation.OperationLogAnnotation)")
    public void logPointcut(){}

    @AfterReturning(value = "logPointcut()",returning = "rtv")
    public void doAfterReturning(JoinPoint joinPoint,Object rtv){
        // TODO 获取当前用户信息
        SecurityUtils.getSubject();
        //通过反射获取织入点方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        log.info("method:" + method);
        //获取方法中注解
        OperationLogAnnotation annotation = method.getAnnotation(OperationLogAnnotation.class);
        String description = annotation.description();
        // 操作类型
        int code = annotation.type().getCode();

        // 删除
        if(code==3){
            List<Integer> bids = (List<Integer>) joinPoint.getArgs()[0];
            List<BookOperationLog> logs = new ArrayList<>();
            for (Integer bid : bids) {
                BookOperationLog log = new BookOperationLog();
                log.setSuccessLog(new Date(),"bigfool",1,code,bid,description);
                logs.add(log);
            }
            bookOperationLogService.saveBatch(logs);
        }

        // 新增|审核
        if(code==1||code==2){
            Book book = (Book) joinPoint.getArgs()[0];
            BookOperationLog log = new BookOperationLog();
            log.setSuccessLog(new Date(),"bigfool",1,code,book.getId(),description);
            bookOperationLogService.save(log);
        }


    }
}
