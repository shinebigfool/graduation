package com.example.graduate.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduate.bean.*;
import com.example.graduate.cache.BeanNameCache;
import com.example.graduate.cache.GroovyInnerCache;
import com.example.graduate.pojo.CalculateRule;
import com.example.graduate.service.CalculateRuleService;
import com.example.graduate.utils.ConfigurationXMLWriter;
import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Configuration
@Slf4j
public class GroovyDynamicLoader implements ApplicationContextAware, InitializingBean {
    @Resource
    private CalculateRuleService calculateRuleService;

    private ConfigurableApplicationContext applicationContext;

    private static final GroovyClassLoader groovyClassLoader = new GroovyClassLoader(GroovyDynamicLoader.class.getClassLoader());

    @Override
    public void afterPropertiesSet() throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("开始解析groovy脚本...");

        init();

        long cost = System.currentTimeMillis() - start;
        System.out.println("结束解析groovy脚本...，耗时：" + cost);
    }
    private void init() {

        List<CalculateRule> calculateRuleDOS = calculateRuleService.list(new LambdaQueryWrapper<CalculateRule>()
                .eq(CalculateRule::getStatus, "ENABLE"));

        List<BeanName> beanNameList = new ArrayList<>();

        List<GroovyInfo> groovyInfos = convert(calculateRuleDOS, beanNameList);

        init(groovyInfos, beanNameList);
    }
    /**
     *
     *
     * @param groovyInfos 存储脚本名称，脚本代码
     * @param beanNameList 存着数据库脚本的相关信息
     * @return void
     */
    private void init(List<GroovyInfo> groovyInfos, List<BeanName> beanNameList) {

        if (CollectionUtils.isEmpty(groovyInfos)) {
            return;
        }

        ConfigurationXMLWriter config = new ConfigurationXMLWriter();
        // xml生成相应的<lang:groovy>标签
        addConfiguration(config, groovyInfos);
        //缓存
        put2map(groovyInfos, beanNameList);

        loadBeanDefinitions(config);
    }
    public void refresh() {

        List<CalculateRule> calculateRuleDOS = calculateRuleService.list(new LambdaQueryWrapper<CalculateRule>()
                .eq(CalculateRule::getStatus, "ENABLE"));

        List<BeanName> beanNameList = new ArrayList<>();

        List<GroovyInfo> groovyInfos = convert(calculateRuleDOS, beanNameList);

        if (CollectionUtils.isEmpty(groovyInfos)) {
            return;
        }

        // loadBeanDefinitions 之后才会生效
        destroyBeanDefinition(groovyInfos);

        destroyScriptBeanFactory();

        ConfigurationXMLWriter config = new ConfigurationXMLWriter();

        addConfiguration(config, groovyInfos);

        put2map(groovyInfos, beanNameList);

        loadBeanDefinitions(config);
    }
    private void loadBeanDefinitions(ConfigurationXMLWriter config) {
        //xml文本
        String contextString = config.getContent();
        System.out.println(contextString);
        if (StringUtils.isBlank(contextString)) {
            return;
        }
        //对applicationContext做了些配置，
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) this.applicationContext.getBeanFactory());
        beanDefinitionReader.setResourceLoader(this.applicationContext);
        beanDefinitionReader.setBeanClassLoader(applicationContext.getClassLoader());
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this.applicationContext));

        beanDefinitionReader.loadBeanDefinitions(new InMemoryResource(contextString));
        //获取自定义处理groovyBean类的bean名称，这往下进到predictBeanType
        String[] postProcessorNames = applicationContext.getBeanFactory().getBeanNamesForType(CustomScriptFactoryPostProcessor.class, true, false);
        //配置自定义处理groovyBeen的类，addBeanPostProcessor()
        for (String postProcessorName : postProcessorNames) {
            applicationContext.getBeanFactory().addBeanPostProcessor((BeanPostProcessor) applicationContext.getBean(postProcessorName));
        }
    }
    private void addConfiguration(ConfigurationXMLWriter config, List<GroovyInfo> groovyInfos) {
        for (GroovyInfo groovyInfo : groovyInfos) {
            writeBean(config, groovyInfo);
        }
    }
    /**
     *
     *
     * @param config 自定义配置写入工具类
     * @param groovyInfo 脚本相关信息
     * @return void
     */
    private void writeBean(ConfigurationXMLWriter config, GroovyInfo groovyInfo) {
        if (checkSyntax(groovyInfo)) {
            DynamicBean bean = composeDynamicBean(groovyInfo);
            config.write(GroovyConstant.SPRING_TAG, bean);
        }
    }
    /**
     * 检查脚本代码是否可以被反射
     *
     * @param groovyInfo 内有脚本String文本
     * @return boolean 是否能反射成为脚本
     */
    private boolean checkSyntax(GroovyInfo groovyInfo) {
        try {
            groovyClassLoader.parseClass(groovyInfo.getGroovyContent());
        } catch (Exception e) {
            log.error(groovyInfo.getClassName()+"解析失败");
            return false;
        }
        return true;
    }
    /**
     * 生成自定义的DynamicBean对象，用于把脚本信息写入xml文件
     * <lang:groovy id="groovyInterface" script-source="classpath:rules/GroovyInterfaceImpl.groovy">
     * id:"脚本名称",script-source:"database:脚本名称"
     * @param groovyInfo
     * @return com.example.demo.groovy.core.DynamicBean
     */
    private DynamicBean composeDynamicBean(GroovyInfo groovyInfo) {
        DynamicBean bean = new DynamicBean();
        String scriptName = groovyInfo.getClassName();
        //判空
        Assert.notNull(scriptName, "parser className cannot be empty!");

        //设置bean的属性，这里只有id和script-source。
        bean.put("id", scriptName);
        bean.put("script-source", GroovyConstant.SCRIPT_SOURCE_PREFIX + scriptName);

        return bean;
    }
    /**
     * 把数据库的脚本详情分解成groovyInfo 和 beanName
     *
     * @param calculateRuleDOS 数据库存的脚本详细信息
     * @param beanNameList 空的数组
     * @return java.util.List<com.example.demo.groovy.cache.GroovyInfo>
     */
    private List<GroovyInfo> convert(List<CalculateRule> calculateRuleDOS, List<BeanName> beanNameList) {

        List<GroovyInfo> groovyInfos = new LinkedList<>();

        if (CollectionUtils.isEmpty(calculateRuleDOS)) {
            return groovyInfos;
        }

        for (CalculateRule calculateRuleDO : calculateRuleDOS) {
            GroovyInfo groovyInfo = new GroovyInfo();
            groovyInfo.setClassName(calculateRuleDO.getBeanName());
            groovyInfo.setGroovyContent(calculateRuleDO.getCalculateRule());
            groovyInfos.add(groovyInfo);

            BeanName beanName = new BeanName();
            beanName.setInterfaceId(calculateRuleDO.getInterfaceId());
            beanName.setBeanName(calculateRuleDO.getBeanName());
            beanNameList.add(beanName);
        }

        return groovyInfos;
    }
    //缓存到map
    private void put2map(List<GroovyInfo> groovyInfos, List<BeanName> beanNameList) {
        //把GroovyInfo缓存到map
        GroovyInnerCache.put2map(groovyInfos);
        //把BeanName缓存到map
        BeanNameCache.put2map(beanNameList);
    }
    public void destroyBeanDefinition(List<GroovyInfo> groovyInfos) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        for (GroovyInfo groovyInfo : groovyInfos) {
            try {
                beanFactory.removeBeanDefinition(groovyInfo.getClassName());
            } catch (Exception e) {
                System.out.println("【Groovy】delete groovy bean definition exception. skip:" + groovyInfo.getClassName());
            }
        }
    }
    public void destroyBeanDefinition(CalculateRule calculateRule){
        if(GroovyInnerCache.getByName(calculateRule.getBeanName())==null){
            return;
        }
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        try {
            GroovyInnerCache.removeMap(calculateRule.getBeanName());
            BeanNameCache.removeMap(calculateRule.getInterfaceId());
            beanFactory.removeBeanDefinition(calculateRule.getBeanName());
        } catch (Exception e) {
            System.out.println("【Groovy】delete groovy bean definition exception. skip:" + calculateRule.getBeanName());
        }
    }
    private void destroyScriptBeanFactory() {
        String[] postProcessorNames = applicationContext.getBeanFactory().getBeanNamesForType(CustomScriptFactoryPostProcessor.class, true, false);
        for (String postProcessorName : postProcessorNames) {
            CustomScriptFactoryPostProcessor processor = (CustomScriptFactoryPostProcessor) applicationContext.getBean(postProcessorName);
            processor.destroy();
        }
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext)applicationContext;
    }
}
