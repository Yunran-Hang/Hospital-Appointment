package com.nbdeyy.config;

import com.nbdeyy.filter.AccessFrequencyFilter;
import com.nbdeyy.filter.ReplaceStreamFilter;
import com.nbdeyy.service.RSAKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;


@Configuration
@Slf4j
public class FilterConfiguration {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RSAKeyService rsaKeyService;

    /**
     * 注册过滤器
     */
    @Bean
    public FilterRegistrationBean<ReplaceStreamFilter> ReplaceStreamFilterRegistration() {
        log.info("开始注册ReplaceStreamFilter过滤器...");
        FilterRegistrationBean<ReplaceStreamFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ReplaceStreamFilter(rsaKeyService));
        registration.addUrlPatterns("/*");
        registration.setName("streamFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<AccessFrequencyFilter> accessFrequencyFilterRegistration() {
        log.info("开始注册AccessFrequencyFilter过滤器...");
        FilterRegistrationBean<AccessFrequencyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AccessFrequencyFilter(redisTemplate));
        // 配置过滤器的拦截的路径
        registration.addUrlPatterns("/user/*");
        registration.setOrder(1);
        return registration;
    }

}
