package com.example.graduate.aop;

import com.example.graduate.pojo.UserPoint;
import com.example.graduate.pojo.UserPointLog;
import com.example.graduate.service.UserPointLogServiceGateway;
import com.example.graduate.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * @author 倪鑫彦
 * @description 记录积分变化日志
 * @since 16:54 2021/3/21
 */
@Aspect
@Component
@Slf4j
public class PointLogAop {
    @Autowired
    private UserPointLogServiceGateway userPointLogServiceGateway;
    @Pointcut("@annotation(com.example.graduate.annotation.PointLogAnnotation)")
    public void logCut(){
    }
    @AfterReturning(value = "logCut()",returning = "rtv")
    public void doAfterReturn(JoinPoint joinPoint, Object rtv){
        saveLog(joinPoint, rtv);
    }
    private void saveLog(JoinPoint joinPoint, Object rtv){
        UserPoint userPoint = (UserPoint) joinPoint.getArgs()[0];
        String note = StringUtil.parseString(joinPoint.getArgs()[1]);
        UserPointLog userPointLog = new UserPointLog();
        userPointLog.setName(userPoint.getName());
        userPointLog.setNote(note);
        userPointLog.setPoint(userPoint.getPoint());
        userPointLogServiceGateway.addLog(userPointLog);
    }
}
