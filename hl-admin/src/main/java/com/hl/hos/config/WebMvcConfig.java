package com.hl.hos.config;

import ch.qos.logback.core.pattern.Converter;
import com.hl.hos.handler.AdminInterceptor;
import com.hl.hos.handler.HosInterceptor;
import com.hl.hos.pojo.Disgnose_info;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{
    @Override
    public void addFormatters(FormatterRegistry registry) {

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        List<String> ex = new ArrayList<>();
        Collections.addAll(ex,"/login","/register","/userLogin","/register_doctor");
        registry.addInterceptor(new HosInterceptor())
                .addPathPatterns("/*").excludePathPatterns(ex);


        //普通用户防止进入管理员页面
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin");
    }
}
