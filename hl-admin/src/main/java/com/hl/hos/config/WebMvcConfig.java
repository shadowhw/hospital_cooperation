package com.hl.hos.config;

import ch.qos.logback.core.pattern.Converter;
import com.hl.hos.pojo.Disgnose_info;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{
    @Override
    public void addFormatters(FormatterRegistry registry) {

    }

}
