package com.example.graduate.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/file/**").addResourceLocations("file:D:/pic/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        /**
         * converters中有很多转换器，其中默认的JSON转换器Jackson转换器有2个，我们使
         * 用FastJson转换器来重写替换Jackson转换器，因此需要将FastJson转换器加入到
         * converters集合中，而add添加是将FastJson转换器加入到了集合尾部，而默认的
         * Jackson转换器位于集合的位置相对靠前，会优先被执行到，因此对于JSON转换还
         * 是会使用Jsckson转换器进行处理，所以如果想要使用FastJson,此处直接将Jackson
         * 转换器从converters集合中移除
         */
        for (int i = converters.size() - 1; i >= 0; i--) {
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                converters.remove(i);
            }
        }

        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.WriteMapNullValue,//是否输出值为null的字段
                SerializerFeature.WriteNullListAsEmpty,//List字段如果为null,输出为[]
                SerializerFeature.WriteNullStringAsEmpty,//字符类型字段如果为null,输出为"",而非null
                SerializerFeature.WriteNullNumberAsZero,//数值字段如果为null,输出为0,而非null
                SerializerFeature.WriteNullBooleanAsFalse//Boolean字段如果为null,输出为false,而非null
        );
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        converters.add(fastJsonHttpMessageConverter);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOrigins("*")
                // 是否允许证书 不再默认开启
                .allowCredentials(true)
                // 设置允许的方法
                .allowedMethods("*")
                // 允许任意头
                .allowedHeaders("*")
                // 跨域允许时间
                .maxAge(3600);
    }
    private CorsConfiguration corsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        /* 请求常用的三种配置，*代表允许所有，当时你也可以自定义属性（比如header只能带什么，只能是post方式等等）
         */
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig());
        return new CorsFilter(source);
    }
//    /**
//     * 跨域配置后swagger2可能不能访问，需要增加如下配置
//     *
//     * @param registry
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
}
