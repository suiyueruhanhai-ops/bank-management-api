// src/main/java/com/bank/bankmanagementapi/config/WebConfig.java

package com.bank.bank_management_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 这是一个配置类
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有路径的请求
        registry.addMapping("/**")
                // 允许来自 Vue 前端开发服务器的请求
                .allowedOrigins("http://localhost:5173") 
                // 允许所有请求方法 (GET, POST, PUT, DELETE...)
                .allowedMethods("*")
                // 允许携带认证信息 (如 Token)
                .allowCredentials(true)
                // 允许所有请求头
                .allowedHeaders("*");
    }
}