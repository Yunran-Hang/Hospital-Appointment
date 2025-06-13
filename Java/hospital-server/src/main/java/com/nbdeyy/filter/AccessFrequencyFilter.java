package com.nbdeyy.filter;

import com.nbdeyy.constant.MessageConstant;
import com.nbdeyy.result.Result;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Slf4j
public class AccessFrequencyFilter implements Filter {

    private final RedisTemplate redisTemplate; // 改用StringRedisTemplate

    // 通过构造函数的方式加载RedisTemplate
    public AccessFrequencyFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 时间窗口（秒）
    private static final long TIME_WINDOW = 1;
    // 允许的最大访问次数
    private static final int MAX_ACCESS_COUNT = 5;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = getClientIp(httpRequest);
        String path = httpRequest.getRequestURI();

        // 使用IP+路径作为键，区分不同接口
        String key = "access_limit:" + clientIp + ":" + path;

        if (isAccessExceeded(key)) {
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setCharacterEncoding("UTF-8"); // 设置字符编码
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

            Result error = Result.error(MessageConstant.TOO_MANY_REQUESTS);
            httpResponse.getWriter().write(error.toJSONString());
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * 判断是否超过访问限制 true是 false否
     * @param key
     * @return
     */
    private boolean isAccessExceeded(String key) {
        // 原子操作：递增并获取新值
        Long count = redisTemplate.opsForValue().increment(key);
        log.info("访问次数：" + count);

        // 首次访问时设置过期时间
        if (count != null && count == 1) {
            redisTemplate.expire(key, TIME_WINDOW, TimeUnit.SECONDS);
        }

        return count != null && count > MAX_ACCESS_COUNT;
    }

    private String getClientIp(HttpServletRequest request) {
        // 保持原有的IP获取逻辑
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}