package com.example.graduate.aop;

import com.example.graduate.annotation.OperationLogAnnotation;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.exception.NxyException;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BookOperationLog;
import com.example.graduate.service.BookOperationLogService;
import com.example.graduate.utils.PresentUserUtils;
import com.example.graduate.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
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
        saveLog(joinPoint,rtv,1);

    }
    @AfterThrowing(value = "logPointcut()",throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint,Throwable e){
        saveLog(joinPoint,null,0);
    }

    private void saveLog(JoinPoint joinPoint,Object rtv,int status){
        String name = PresentUserUtils.qryPresentUserAccount();
        if(StringUtil.isBlank(name)){
            log.error("非法操作");
            return;
        }
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
                log.setSuccessLog(name,status,code,bid,description);
                logs.add(log);
            }
            bookOperationLogService.saveBatch(logs);
        }

        // 新增|审核
        if(code==1||code==2){
            Book book = (Book) joinPoint.getArgs()[0];
            BookOperationLog log = new BookOperationLog();
            int id = book.getId();
            if(code==1){
                id = StringUtil.objectToInt(((DTO)rtv).getRetMsg());
            }
            log.setSuccessLog(name,status,code,id,description);
            bookOperationLogService.save(log);
        }
    }
}
