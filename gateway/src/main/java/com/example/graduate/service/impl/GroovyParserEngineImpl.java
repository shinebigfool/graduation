package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduate.bean.RuleEngineRequest;
import com.example.graduate.cache.BeanNameCache;
import com.example.graduate.cache.GroovyInnerCache;
import com.example.graduate.cache.RuleCache;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.pojo.CalculateRule;
import com.example.graduate.service.CalculateParser;
import com.example.graduate.service.CalculateRuleService;
import com.example.graduate.service.GroovyParserEngine;
import com.example.graduate.utils.GroovyScriptUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class GroovyParserEngineImpl implements GroovyParserEngine,
        ApplicationContextAware,
        InitializingBean {
    private ApplicationContext applicationContext;
    @Autowired
    private CalculateRuleService calculateRuleService;
    /**
     * search
     *
     * @param request 包含指定脚本id和参数
     * @return com.example.graduate.dto.DTO
     */
    @Override
    public DTO parse2DTO(RuleEngineRequest request) {
        //beanName
        String beanName = BeanNameCache.getByInterfaceId(request.getInterfaceId());
        //如果bean缓存中有该脚本，用bean的方法运行
        if(beanName!=null&&GroovyInnerCache.getByName(beanName)!=null){
            System.out.println(applicationContext.getBean(beanName).getClass());

            CalculateParser bean = (CalculateParser) applicationContext.getBean(beanName);
            return bean.parse2DTO(request.getParams());
        }
        //如果invoke缓存中有该脚本，跳过查数据库和解析步骤
        CalculateParser script = RuleCache.getById(request.getInterfaceId());
        if(script!=null){
            return GroovyScriptUtil.invokeByGroovyClassLoader(script,request.getParams());
        }
        //缓存中没有该脚本，先查出相关脚本，然后交给invoke工具类处理
        LambdaQueryWrapper<CalculateRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CalculateRule::getInterfaceId,request.getInterfaceId());

        try {
            CalculateRule rule = calculateRuleService.getOne(wrapper);
            return GroovyScriptUtil.invokeByGroovyClassLoader(rule,request.getParams());
        }catch (Exception e){
            return new DTO(RetCodeEnum.EXCEPTION.getCode(),e.getMessage());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
