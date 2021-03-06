package com.example.graduate.aop;

import com.example.graduate.annotation.OperationLogAnnotation;
import com.example.graduate.dto.DTO;
import com.example.graduate.pojo.Book;
import com.example.graduate.pojo.BookOperationLog;
import com.example.graduate.service.BookOperationLogService;
import com.example.graduate.utils.PresentUserUtils;
import com.example.graduate.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Map;

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
    public void logPointcut() {
    }

    @AfterReturning(value = "logPointcut()", returning = "rtv")
    public void doAfterReturning(JoinPoint joinPoint, Object rtv) {
        saveLog(joinPoint, rtv);

    }

    private void saveLog(JoinPoint joinPoint, Object rtv) {
        String name = PresentUserUtils.qryPresentUserAccount();
        if (StringUtil.isBlank(name)) {
            name = "";
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
        int ifSuccess = ((DTO) rtv).getRetCode().equals("000000") ? 1 : 0;
        // 删除
        if (code == 3) {
            Map<String,Object> map = (Map<String, Object>) joinPoint.getArgs()[0];
            int bid = StringUtil.objectToInt(map.get("bid"));

            BookOperationLog log = new BookOperationLog();
            log.setSuccessLog(name, ifSuccess, code, bid, description);
            bookOperationLogService.save(log);
        }

        // 新增|审核
        if (code == 1 || code == 2) {
            Book book = (Book) joinPoint.getArgs()[0];
            BookOperationLog log = new BookOperationLog();
            int id = book.getId();
//            if (code == 1) {
//                id = StringUtil.objectToInt(((DTO) rtv).getRetMsg());
//            }
            log.setSuccessLog(name, ifSuccess, code, id, description);
            bookOperationLogService.save(log);
        }
    }
}
