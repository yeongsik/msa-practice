package com.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.base-path}")
    private String uploadBasePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** 요청을 로컬 파일 시스템의 uploadBasePath 경로로 매핑
        String resourcePath = "file:" + System.getProperty("user.dir") + "/" + uploadBasePath + "/";
        
        // uploadBasePath가 절대 경로인 경우 처리 (optional)
        if (uploadBasePath.startsWith("/")) {
            resourcePath = "file:" + uploadBasePath + "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourcePath);
    }
}
