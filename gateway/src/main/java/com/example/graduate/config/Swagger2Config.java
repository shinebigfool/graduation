package com.example.graduate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
//注解开启 swagger2 功能
@EnableSwagger2
public class Swagger2Config {


    @Value("${swagger.basePackage}")
    private String basePackage;

    @Value("${swagger.title}")
    private String title;

    @Value("${swagger.version}")
    private String version;

    @Value("${swagger.description}")
    private String description;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //为当前包路径
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build()
                //.globalOperationParameters(globalOperation());
                .securitySchemes(security());
    }

    private List<ApiKey> security() {
        List<ApiKey> apiKeys = new ArrayList<>();
        ApiKey apiKey = new ApiKey("access_token", "access_token", "header");
        // ApiKey apiKey1 = new ApiKey("refresh_token", "refresh_token", "header");
        apiKeys.add(apiKey);
        //apiKeys.add(apiKey1);
        return apiKeys;
    }



    //构建 api文档的详细信息函数,注意这里的注解引用的是哪个
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title(title)
                //创建人
//                .contact(new Contact(name, url, email))
                //版本号
                .version(version)
                //描述
                .description(description)
                .build();
    }

}
