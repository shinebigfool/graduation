package com.example.graduate.utils;

import com.baomidou.mybatisplus.core.toolkit.IOUtils;
import com.example.graduate.bean.DynamicBean;
import com.example.graduate.bean.GroovyConstant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * @author 倪鑫彦
 * @description 将DynamicBean表示的Spring Bean信息写入到Spring配置文件中。
 * 配置文件以Document对象保存在内存中，并不真正IO输出到本地文件系统。
 * 作用：写xml
 * 成员变量：content存储xml文本，content的量被取走之后，此对象便没有引用了
 * @since 9:27 2020/12/12
 */
public class ConfigurationXMLWriter {
    public static String DDD = "http://apache.org/xml/features/disallow-doctype-decl";

    /**
     * 配置文件内容
     */
    private String content;

    /**
     * 配置文件文档类
     */
    private Document document = null;
    /**
     * 初始化document，给xml文件写入初始值<beans></beans>
     * 给解析器配置了一些参数(是否校验格式)
     * @throws Exception
     */
    private void initDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(DDD, true);
        // 由此代码生成的解析器将验证被解析的文档
        factory.setValidating(true);
        factory.setNamespaceAware(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        // xml文件的初始值
        StringReader reader = new StringReader(GroovyConstant.BEANS_FILE_CONTENT);
        this.document = builder.parse(new InputSource(reader));
    }
    /**
     * 构造函数，初始化xml文件，写xml的头部信息，最外层的标签
     */
    public ConfigurationXMLWriter() {
        try {
            initDocument();
        } catch (Exception e) {
            throw new RuntimeException("algo script load error");
        }
    }
    /**
     * 根据Id查找节点(单个脚本的标签节点，如下，id=helloService)
     * <lang:groovy id="helloService" script-source="classpath:rules/HelloServiceImpl.groovy">
     *     <lang:property name="name" value="maple"></lang:property>
     *  </lang:groovy>
     * @param id xml文件中以id区分每一个从数据库里读出来的groovy脚本
     * @return
     */
    private Node getElementById(String id) {
        //获取标签为<lang:groovy>的节点
        NodeList list = document.getElementsByTagName(GroovyConstant.SPRING_TAG);
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getAttributes().getNamedItem("id").getNodeValue().equals(id)) {
                return list.item(i);
            }
        }
        return null;
    }
    /**
     * 检查指定的bean是否已经在配置文件中
     *
     * @param beanName 脚本名称
     * @return
     */
    public boolean isExist(String beanName) {
        try {
            Node bean = this.getElementById(beanName);
            return (bean != null);
        } catch (Exception e) {
            throw new RuntimeException("write algo script error");
        }
    }
    /**
     * 执行XML文档操作
     * @param tagName     标签名<lang:groovy>
     * @param dynamicBean 动态bean id:"脚本名称",script-source:"database:脚本名称"
     * 生成：
     * <lang:groovy id="groovyInterface" script-source="classpath:rules/GroovyInterfaceImpl.groovy">
     * </lang:groovy>
     * 放在<beans></beans>里面
     */
    private void doWrite(String tagName, DynamicBean dynamicBean) {
        try {
            Element bean = document.createElement(tagName);
            Iterator<String> iterator = dynamicBean.keyIterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                bean.setAttribute(key, dynamicBean.get(key));
            }
            NodeList list = document.getElementsByTagName("beans");
            Node beans = list.item(0);
            beans.appendChild(bean);
        } catch (Exception e) {
            throw new RuntimeException("algo script load error");
        }
    }
    /**
     * 将bean写入配置文件
     *
     * @param tagName     标签
     * @param dynamicBean 动态bean信息
     */
    public void write(String tagName, DynamicBean dynamicBean) {
        try {
            //如果配置文件中已经存在，则直接返回
            if (isExist(dynamicBean.get("id"))) {
                return;
            }

            this.doWrite(tagName, dynamicBean);
            this.saveDocument();

        } catch (Exception e) {
            throw new RuntimeException("algo script load error");
        }
    }
    /**
     * 保存Document
     * 把xml写成String
     * @throws Exception
     */
    private void saveDocument() throws Exception {
        DOMSource source = new DOMSource(this.document);
        StringWriter writer = new StringWriter();
        try {
            Result result = new StreamResult(writer);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
            //这里把document写成String，存入content
            this.content = writer.getBuffer().toString();
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
    /**
     * Getter method for property <tt>content</tt>.
     *
     * @return property value of content
     */
    public String getContent() {
        return content;
    }
}
