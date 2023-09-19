package com.ai.gardening.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Value("${allowed.origin}")
    private String allowedOrigin;

    @Value("${allowed.origin.www}")
    private String allowedOriginWithWWW;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins(allowedOrigin, allowedOriginWithWWW)
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
