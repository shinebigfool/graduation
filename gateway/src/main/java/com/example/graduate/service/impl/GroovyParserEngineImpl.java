package com.example.graduate.service.impl;

import com.example.graduate.bean.RuleEngineRequest;
import com.example.graduate.cache.BeanNameCache;
import com.example.graduate.dto.DTO;
import com.example.graduate.service.CalculateParser;
import com.example.graduate.service.GroovyParserEngine;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class GroovyParserEngineImpl implements GroovyParserEngine,
        ApplicationContextAware,
        InitializingBean {
    private ApplicationContext applicationContext;
    @Override
    public DTO parse2DTO(RuleEngineRequest request) {
        String beanName = BeanNameCache.getByInterfaceId(request.getInterfaceId());
        CalculateParser bean = (CalculateParser) applicationContext.getBean(beanName);

        return bean.parse2DTO(request.getParams());
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
