package com.example.graduate.aop;

import com.example.graduate.annotation.AffairLogAnnotation;
import com.example.graduate.dto.DTO;
import com.example.graduate.pojo.Affair;
import com.example.graduate.pojo.AffairLog;
import com.example.graduate.service.AffairLogServiceGateway;
import com.example.graduate.utils.PresentUserUtils;
import com.example.graduate.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author 倪鑫彦
 * @description 切入流程相关接口，记录流程变更记录
 * @since 10:20 2021/3/10
 */
@Aspect
@Component
@Slf4j
public class AffairAop {
    @Autowired
    private AffairLogServiceGateway affairLogServiceGateway;
    @Pointcut("@annotation(com.example.graduate.annotation.AffairLogAnnotation)")
    public void logPointcut() {
    }
    @AfterReturning(value = "logPointcut()", returning = "rtv")
    public void doAfterReturning(JoinPoint joinPoint, Object rtv) {
        //操作失败不记录
        if(!((DTO) rtv).getRetCode().equals("000000")){
            return;
        }
        //通过反射获取织入点方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        log.info("method:" + method);
        //获取方法中注解
        AffairLogAnnotation affairLogAnnotation = method.getAnnotation(AffairLogAnnotation.class);
        // 操作类型
        int code = affairLogAnnotation.type().getCode();
        if(code==3){
            delLog(joinPoint);
        }
        if(code==1||code==11){
            saveLog(joinPoint,code);
        }
    }
    private void saveLog(JoinPoint joinPoint,Integer type){

        String name = PresentUserUtils.qryPresentUserAccount();
        if (StringUtil.isBlank(name)) {
            name = "";
        }
        Affair affair = (Affair) joinPoint.getArgs()[0];
        AffairLog affairLog = new AffairLog();
        affairLog.setAffairId(affair.getId());
        affairLog.setOperateAccount(name);
        affairLog.setOperateDetail(affair.getNote());
        affairLog.setOperateType(type);
        affairLogServiceGateway.addAffairLog(affairLog);
    }
    private void delLog(JoinPoint joinPoint){
        log.info("执行删除逻辑");
        Map<String,Object> map = (Map<String, Object>) joinPoint.getArgs()[0];
        int affairId = StringUtil.objectToInt(map.get("affairId"));
        affairLogServiceGateway.delByAffairId(affairId);
    }
}
