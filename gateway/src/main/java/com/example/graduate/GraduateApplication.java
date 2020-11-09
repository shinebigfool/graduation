package com.example.graduate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan(basePackages = "com.example.graduate.mappers")
@ComponentScan(basePackages = "com.example")
@EnableSwagger2
@Configuration

public class GraduateApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduateApplication.class, args);
    }

}
