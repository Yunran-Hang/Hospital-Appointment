package com.nbdeyy.config;

import com.nbdeyy.interceptor.JwtTokenAdminInterceptor;
import com.nbdeyy.interceptor.JwtTokenUserInterceptor;
import com.nbdeyy.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 注册自定义拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");

        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login");

        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/login")
                .excludePathPatterns("/user/user/register")
                .excludePathPatterns("/user/user/captcha/verify")
                .excludePathPatterns("/user/category")
                .excludePathPatterns("/user/doctor/list")
                .excludePathPatterns("/user/schedule/*")
                .excludePathPatterns("/user/doctor/*");
    }

    /**
     * 扩展Spring MVC框架的消息转化器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        
        // 创建自定义转换器
        MappingJackson2HttpMessageConverter customConverter = new MappingJackson2HttpMessageConverter() {
            @Override
            public boolean canWrite(Class<?> clazz, MediaType mediaType) {
                // 获取当前请求路径
                try {
                    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attributes != null) {
                        String requestURI = attributes.getRequest().getRequestURI();
                        // 排除knife4j和springdoc相关路径
                        if (requestURI.contains("/v3/api-docs") || 
                            requestURI.contains("/swagger-ui") || 
                            requestURI.contains("/swagger-resources") ||
                            requestURI.contains("/webjars") ||
                            requestURI.contains("/doc.html")) {
                            return false;
                        }
                    }
                } catch (Exception e) {
                    // 如果获取请求失败，继续使用自定义转换器
                }
                return super.canWrite(clazz, mediaType);
            }
        };
        
        customConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0, customConverter);
    }
    

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 使用 allowedOriginPatterns 替代 allowedOrigins
                .allowedMethods("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
//package com.nbdeyy.config;
//
//import com.nbdeyy.interceptor.JwtTokenAdminInterceptor;
//import com.nbdeyy.interceptor.JwtTokenUserInterceptor;
//import com.nbdeyy.json.JacksonObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.web.servlet.config.annotation.*;
//
//
//import java.util.List;
//
///**
// * 配置类，注册web层相关组件
// */
//@Configuration
//@Slf4j
//public class WebMvcConfiguration extends WebMvcConfigurationSupport {
//    @Autowired
//    private JwtTokenUserInterceptor jwtTokenUserInterceptor;
//
//    @Autowired
//    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;
//
//    /**
//     * 注册自定义拦截器
//     *
//     * @param registry
//     */
//    protected void addInterceptors(InterceptorRegistry registry) {
//        log.info("开始注册自定义拦截器...");
//
//        registry.addInterceptor(jwtTokenAdminInterceptor)
//                .addPathPatterns("/admin/**")
//                .excludePathPatterns("/admin/login");
//
//        registry.addInterceptor(jwtTokenUserInterceptor)
//                .addPathPatterns("/user/**")
//                .excludePathPatterns("/user/user/login") // 登录
//                .excludePathPatterns("/user/user/register") // 注册
//                .excludePathPatterns("/user/user/captcha/verify") // 验证码验证
//                .excludePathPatterns("/user/category") // 获取分类
//                .excludePathPatterns("/user/doctor/list") // 获取医生列表
//                .excludePathPatterns("/user/schedule/*")
//                .excludePathPatterns("/user/doctor/*");
//    }
//
//    /**
//     * 扩展Spring MVC框架的消息转化器
//     * @param converters
//     */
//    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        log.info("扩展消息转换器...");
//        // 创建一个消息转化器
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        // 需要为消息转换器设置一个对象转换器，对象转换器可以将Java对象序列化为json数据
//        converter.setObjectMapper(new JacksonObjectMapper());
//        // 加入自己的消息转换器
//        converters.add(0,converter);
//    }
//
//
//}
//
