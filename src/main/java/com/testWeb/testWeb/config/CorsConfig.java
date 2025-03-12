package com.testWeb.testWeb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${frontend.url}")
    private String frontendUrl;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Existing CORS configuration for API upload
        registry.addMapping("/api/v1/upload") // Разрешаем доступ только для указанного пути
                .allowedOrigins("{frontend.url}") // Указываем источник (ваш фронтенд)
                .allowedMethods("POST") // Разрешаем только метод POST
                .allowedHeaders("*"); // Разрешаем все заголовки
    }
}

