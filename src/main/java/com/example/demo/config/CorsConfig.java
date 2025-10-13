package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    // 원래의 Cors 설정을 유지하되, 빈 이름이 기본인 'corsConfigurer'와 충돌하지 않도록
    // 메서드명을 변경하여 빈 이름을 고유하게 만듭니다.
    @Bean
    public WebMvcConfigurer corsConfigurerCustom() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost", "http://localhost:8080") // 프론트 도메인 허용 (포트 포함)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
