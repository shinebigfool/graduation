package com.example.graduate.config;

import com.example.graduate.bean.DatabaseScriptSource;
import com.example.graduate.bean.GroovyConstant;
import com.example.graduate.utils.StringUtil;
import net.sf.cglib.asm.Type;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.InterfaceMaker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.Conventions;
import org.springframework.core.Ordered;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.RefreshableScriptTargetSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class CustomScriptFactoryPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
    implements
        BeanClassLoaderAware,
        BeanFactoryAware,
        ResourceLoaderAware,
        DisposableBean,
        Ordered {
    //把groovy脚本写在xml里
    protected static final String INLINE_SCRIPT_PREFIX = "inline:";
    //
    protected static final String SCRIPT_FACTORY_NAME_PREFIX = "scriptFactory.";

    protected static final String SCRIPTED_OBJECT_NAME_PREFIX = "scriptedObject.";

    protected static final String REFRESH_CHECK_DELAY_ATTRIBUTE = Conventions
            .getQualifiedAttributeName(
                    ScriptFactoryPostProcessor.class,
                    "refreshCheckDelay");
    private long defaultRefreshCheckDelay = -1;

    private ClassLoader beanClassLoader = ClassUtils
            .getDefaultClassLoader();

    private ConfigurableBeanFactory beanFactory;

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    //这个东西存储bean beanName Object
    protected final DefaultListableBeanFactory scriptBeanFactory = new DefaultListableBeanFactory();

    /**
     * Map from bean name String to ScriptSource object
     */
    private final Map<String, ScriptSource> scriptSourceCache = new ConcurrentHashMap<>();
    /**
     * 继承自BeanClassLoaderAware
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    /**
     * 继承自BeanFactoryAware
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException(
                    "ScriptFactoryPostProcessor doesn't work with a BeanFactory "
                            + "which does not implement ConfigurableBeanFactory: "
                            + beanFactory.getClass());
        }
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;

        // Required so that references (up container hierarchies) are correctly resolved.
        this.scriptBeanFactory.setParentBeanFactory(this.beanFactory);

        // Required so that all BeanPostProcessors, Scopes, etc become available.
        this.scriptBeanFactory.copyConfigurationFrom(this.beanFactory);

        // Filter out BeanPostProcessors that are part of the AOP infrastructure,
        // since those are only meant to apply to beans defined in the original factory.
        this.scriptBeanFactory.getBeanPostProcessors().removeIf(beanPostProcessor -> beanPostProcessor instanceof AopInfrastructureBean);
    }
    /**
     * 继承自ResourceLoaderAware
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    /**
     * 继承自DisposableBean
     */
    @Override
    public void destroy() {
        this.scriptBeanFactory.destroySingletons();
    }
    /**
     * 继承自Ordered
     */
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    /**
     * 重写InstantiationAwareBeanPostProcessorAdapter的方法
     * 返回bean的class，执行很多次
     * 在这里会读取脚本的代码
     */
    @Override
    public Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
        // We only apply special treatment to ScriptFactory implementations here.
        if (!ScriptFactory.class.isAssignableFrom(beanClass)) {
            return null;
        }
        BeanDefinition bd = this.beanFactory.getMergedBeanDefinition(beanName);
        try {
            String scriptFactoryBeanName = SCRIPT_FACTORY_NAME_PREFIX + beanName;
            String scriptedObjectBeanName = SCRIPTED_OBJECT_NAME_PREFIX + beanName;
            prepareScriptBeans(bd, scriptFactoryBeanName, scriptedObjectBeanName);

            ScriptFactory scriptFactory = this.scriptBeanFactory.getBean(scriptFactoryBeanName,
                    ScriptFactory.class);
            ScriptSource scriptSource = getScriptSource(scriptFactoryBeanName,
                    scriptFactory.getScriptSourceLocator());
            Class[] interfaces = scriptFactory.getScriptInterfaces();

            //这一步读取脚本的class并返回
            Class scriptedType = scriptFactory.getScriptedObjectType(scriptSource);
            if (scriptedType != null) {
                return scriptedType;
            } else if (!ObjectUtils.isEmpty(interfaces)) {
                return (interfaces.length == 1 ? interfaces[0]
                        : createCompositeInterface(interfaces));
            } else {
                if (bd.isSingleton()) {
                    return this.scriptBeanFactory.getBean(scriptedObjectBeanName).getClass();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 这个方法在bean被调用之前不会主动触发
     * 返回bean实例，替代之前产生的bean，只执行一次
     * 刷新缓存之后再调用某bean则又会调用该方法
     * 这个方法只处理脚本bean
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        // We only apply special treatment to
        //  ScriptFactory implementations here.
        if (!ScriptFactory.class.isAssignableFrom(beanClass)) {
            return null;
        }
        BeanDefinition bd = this.beanFactory.getMergedBeanDefinition(beanName);
        String scriptFactoryBeanName = SCRIPT_FACTORY_NAME_PREFIX + beanName;
        String scriptedObjectBeanName = SCRIPTED_OBJECT_NAME_PREFIX + beanName;
        prepareScriptBeans(bd, scriptFactoryBeanName, scriptedObjectBeanName);

        ScriptFactory scriptFactory = this.scriptBeanFactory.getBean(scriptFactoryBeanName,
                ScriptFactory.class);
        ScriptSource scriptSource = getScriptSource(scriptFactoryBeanName,
                scriptFactory.getScriptSourceLocator());
        boolean isFactoryBean = false;
        try {
            Class scriptedObjectType = scriptFactory.getScriptedObjectType(scriptSource);
            // Returned type may be null if the factory is unable to determine the type.
            if (scriptedObjectType != null) {
                isFactoryBean = FactoryBean.class.isAssignableFrom(scriptedObjectType);
            }
        } catch (Exception ex) {
            throw new BeanCreationException(beanName,
                    "Could not determine scripted object type for " + scriptFactory, ex);
        }

        long refreshCheckDelay = resolveRefreshCheckDelay(bd);
        if (refreshCheckDelay >= 0) {
            Class[] interfaces = scriptFactory.getScriptInterfaces();
            RefreshableScriptTargetSource ts = new RefreshableScriptTargetSource(
                    this.scriptBeanFactory, scriptedObjectBeanName, scriptFactory, scriptSource,
                    isFactoryBean);
            ts.setRefreshCheckDelay(refreshCheckDelay);
            return createRefreshableProxy(ts, interfaces);
        }
        if (isFactoryBean) {
            scriptedObjectBeanName = BeanFactory.FACTORY_BEAN_PREFIX + scriptedObjectBeanName;
        }
        return this.scriptBeanFactory.getBean(scriptedObjectBeanName);
    }

    /**
     * 脚本name，class，factory三者的注册
     * 有缓存机制，后续将跳过此方法主体
     * Prepare the script beans in the internal BeanFactory that this
     * post-processor uses. Each original bean definition will be split
     * into a ScriptFactory definition and a scripted object definition.
     * bean->script factory+script object
     * 通过scriptBeanFactory.registerBeanDefinition方法
     * 把bean存入scriptBeanFactory中
     * bean的存放形式：beanName和object的键值对
     * String scriptedObjectBeanName = SCRIPTED_OBJECT_NAME_PREFIX + beanName;
     * BeanDefinition
     * @param bd                     脚本定义
     * @param scriptFactoryBeanName  脚本工厂名
     * @param scriptedObjectBeanName 脚本名
     */
    protected void prepareScriptBeans(BeanDefinition bd, String scriptFactoryBeanName,
                                      String scriptedObjectBeanName){

        // Avoid recreation of the script bean definition in case of a prototype.
        synchronized (this.scriptBeanFactory) {
            if(!this.scriptBeanFactory.containsBeanDefinition(scriptedObjectBeanName)){
                //把脚本放到scriptBeanFactory
                this.scriptBeanFactory.registerBeanDefinition(scriptFactoryBeanName,
                        createScriptFactoryBeanDefinition(bd));
                ScriptFactory scriptFactory = this.scriptBeanFactory.getBean(scriptFactoryBeanName,
                        ScriptFactory.class);
                ScriptSource scriptSource = getScriptSource(scriptFactoryBeanName,
                        scriptFactory.getScriptSourceLocator());
                Class[] interfaces = scriptFactory.getScriptInterfaces();

                Class[] scriptedInterfaces = interfaces;
                if (scriptFactory.requiresConfigInterface() && !bd.getPropertyValues().isEmpty()) {
                    Class configInterface = createConfigInterface(bd, interfaces);
                    scriptedInterfaces = (Class[]) ObjectUtils.addObjectToArray(interfaces,
                            configInterface);
                }
                BeanDefinition objectBd = createScriptedObjectBeanDefinition(bd,
                        scriptFactoryBeanName, scriptSource, scriptedInterfaces);
                long refreshCheckDelay = resolveRefreshCheckDelay(bd);
                if (refreshCheckDelay >= 0) {
                    objectBd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
                }
                this.scriptBeanFactory.registerBeanDefinition(scriptedObjectBeanName, objectBd);
            }
        }
    }
    /**
     * Create a ScriptFactory bean definition based on the given script definition,
     * extracting only the definition data that is relevant for the ScriptFactory
     * (that is, only bean class and constructor arguments).
     * 过滤了一下bd，取只与ScriptFactory有关的数据
     * @param bd the full script bean definition
     * @return the extracted ScriptFactory bean definition
     * @see org.springframework.scripting.ScriptFactory
     */
    protected BeanDefinition createScriptFactoryBeanDefinition(BeanDefinition bd) {
        GenericBeanDefinition scriptBd = new GenericBeanDefinition();
        scriptBd.setBeanClassName(bd.getBeanClassName());
        scriptBd.getConstructorArgumentValues()
                .addArgumentValues(bd.getConstructorArgumentValues());
        return scriptBd;
    }
    /**
     * 获取脚本名称
     * 取database:后面的字符串（脚本名称）
     * script-source="database:rewardCalculateParser"
     * @param scriptSourceLocator  script-source=""双引号里面的内容
     * @return rewardCalculateParser
     */
    private String getScriptName(String scriptSourceLocator) {
        return StringUtils.substringAfter(scriptSourceLocator,
                GroovyConstant.SCRIPT_SOURCE_PREFIX);
    }
    /**
     * 根据beanName转换成ScriptSource
     * 这里主要看else if里的逻辑，处理自定义database:数据源脚本的方法
     * @param beanName 脚本名称
     * @param scriptSourceLocator  script-source=""双引号里面的内容，
     * @param loader
     * @return
     */
    protected ScriptSource convertToScriptSource(String beanName, String scriptSourceLocator,
                                                 ResourceLoader loader) {
        if (scriptSourceLocator.startsWith(INLINE_SCRIPT_PREFIX)) {
            return new StaticScriptSource(scriptSourceLocator.substring(INLINE_SCRIPT_PREFIX
                    .length()), beanName);
        } else if (scriptSourceLocator.startsWith(GroovyConstant.SCRIPT_SOURCE_PREFIX)) {
            String scriptName = getScriptName(scriptSourceLocator);
            //这一步通过构造函数，传入脚本名，直接把对应脚本从生成，到缓存全部完成
            return new DatabaseScriptSource(scriptName);
        } else {
            return new ResourceScriptSource(loader.getResource(scriptSourceLocator));
        }
    }
    /**
     * Obtain a ScriptSource for the given bean, lazily creating it
     * if not cached already.
     *
     * @param beanName            the name of the scripted bean
     * @param scriptSourceLocator the script source locator associated with the bean
     * @return the corresponding ScriptSource instance
     * @see #convertToScriptSource
     */
    protected ScriptSource getScriptSource(String beanName, String scriptSourceLocator) {
        ScriptSource scriptSource = this.scriptSourceCache.get(beanName);
        if (scriptSource == null) {
            scriptSource = convertToScriptSource(beanName, scriptSourceLocator, this.resourceLoader);
            //把脚本缓存起来key->脚本名,value->封装好的脚本
            this.scriptSourceCache.put(beanName, scriptSource);
        }

        return scriptSource;
    }
    /**
     * 使用CGLIB接口创建实例
     * @param bd         the bean definition (property values etc) to create a
     *                   config interface for
     * @param interfaces the interfaces to check against (might define
     *                   getters corresponding to the setters we're supposed to generate)
     * @return the config interface
     * @see net.sf.cglib.proxy.InterfaceMaker
     * @see org.springframework.beans.BeanUtils#findPropertyType
     */
    protected Class createConfigInterface(BeanDefinition bd, Class[] interfaces) {
        InterfaceMaker maker = new InterfaceMaker();
        PropertyValue[] pvs = bd.getPropertyValues().getPropertyValues();
        for (PropertyValue pv : pvs) {
            String propertyName = pv.getName();
            Class propertyType = BeanUtils.findPropertyType(propertyName, interfaces);
            String setterName = "set" + StringUtils.capitalize(propertyName);
            Signature signature = new Signature(setterName, Type.VOID_TYPE,
                    new Type[]{Type.getType(propertyType)});
            maker.add(signature, new Type[0]);
        }
        if (bd instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition abd = (AbstractBeanDefinition) bd;
            if (abd.getInitMethodName() != null) {
                Signature signature = new Signature(abd.getInitMethodName(), Type.VOID_TYPE,
                        new Type[0]);
                maker.add(signature, new Type[0]);
            }
            if (abd.getDestroyMethodName() != null) {
                Signature signature = new Signature(abd.getDestroyMethodName(), Type.VOID_TYPE,
                        new Type[0]);
                maker.add(signature, new Type[0]);
            }
        }
        return maker.create();
    }

    /**
     * 基于传入的BeanDefinition，进行一些加工
     * Create a bean definition for the scripted object, based on the given script
     * definition, extracting the definition data that is relevant for the scripted
     * object (that is, everything but bean class and constructor arguments).
     *
     * @param bd                    the full script bean definition
     * @param scriptFactoryBeanName the name of the internal ScriptFactory bean
     * @param scriptSource          the ScriptSource for the scripted bean
     * @param interfaces            the interfaces that the scripted bean is supposed to implement
     * @return the extracted ScriptFactory bean definition
     * @see org.springframework.scripting.ScriptFactory#getScriptedObject
     */
    protected BeanDefinition createScriptedObjectBeanDefinition(BeanDefinition bd,
                                                                String scriptFactoryBeanName,
                                                                ScriptSource scriptSource,
                                                                Class[] interfaces) {

        GenericBeanDefinition objectBd = new GenericBeanDefinition(bd);
        objectBd.setFactoryBeanName(scriptFactoryBeanName);
        objectBd.setFactoryMethodName("getScriptedObject");
        objectBd.getConstructorArgumentValues().clear();
        objectBd.getConstructorArgumentValues().addIndexedArgumentValue(0, scriptSource);
        objectBd.getConstructorArgumentValues().addIndexedArgumentValue(1, interfaces);
        return objectBd;
    }
    /**
     * Get the refresh check delay for the given {@link ScriptFactory} {@link BeanDefinition}.
     * If the {@link BeanDefinition} has a
     * {@link org.springframework.core.AttributeAccessor metadata attribute}
     * under the key {@link #REFRESH_CHECK_DELAY_ATTRIBUTE} which is a valid {@link Number}
     * type, then this value is used. Otherwise, the the {@link #defaultRefreshCheckDelay}
     * value is used.
     *
     * @param beanDefinition the BeanDefinition to check
     * @return the refresh check delay
     */
    protected long resolveRefreshCheckDelay(BeanDefinition beanDefinition) {
        long refreshCheckDelay = this.defaultRefreshCheckDelay;
        Object attributeValue = beanDefinition.getAttribute(REFRESH_CHECK_DELAY_ATTRIBUTE);
        if (attributeValue instanceof Number) {
            refreshCheckDelay = ((Number) attributeValue).longValue();
        } else if (attributeValue instanceof String) {
            refreshCheckDelay = Long.parseLong((String) attributeValue);
        } else if (attributeValue != null) {
            throw new BeanDefinitionStoreException("Invalid refresh check delay attribute ["
                    + REFRESH_CHECK_DELAY_ATTRIBUTE
                    + "] with value [" + attributeValue
                    + "]: needs to be of type Number or String");
        }
        return refreshCheckDelay;
    }
    /**
     * Create a composite interface Class for the given interfaces,
     * implementing the given interfaces in one single Class.
     * <p>The default implementation builds a JDK proxy class
     * for the given interfaces.
     *
     * @param interfaces the interfaces to merge
     * @return the merged interface as Class
     * @see java.lang.reflect.Proxy#getProxyClass
     */
    protected Class createCompositeInterface(Class[] interfaces) {
        return ClassUtils.createCompositeInterface(interfaces, this.beanClassLoader);
    }

    /**
     * Create a refreshable proxy for the given AOP TargetSource.
     * 可以用来刷缓存的代理
     * @param ts         the refreshable TargetSource
     * @param interfaces the proxy interfaces (may be <code>null</code> to
     *                   indicate proxying of all interfaces implemented by the target class)
     * @return the generated proxy
     * @see RefreshableScriptTargetSource
     */
    protected Object createRefreshableProxy(TargetSource ts, Class[] interfaces) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource(ts);

        if (interfaces == null) {
            proxyFactory.setInterfaces(ClassUtils.getAllInterfacesForClass(ts.getTargetClass(),
                    this.beanClassLoader));
        } else {
            proxyFactory.setInterfaces(interfaces);
        }

        DelegatingIntroductionInterceptor introduction = new DelegatingIntroductionInterceptor(ts);
        introduction.suppressInterface(TargetSource.class);
        proxyFactory.addAdvice(introduction);

        return proxyFactory.getProxy(this.beanClassLoader);
    }
}
