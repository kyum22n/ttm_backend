package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.interceptor.LoginCheckInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
  @Autowired
  private LoginCheckInterceptor loginCheckInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
      .addInterceptor(loginCheckInterceptor)
      .addPathPatterns("/**")
      .excludePathPatterns("/image/**", "/css/**","/js/**");
  }
}
