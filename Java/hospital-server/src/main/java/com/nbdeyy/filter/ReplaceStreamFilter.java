package com.nbdeyy.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nbdeyy.constant.FeignConstant;
import com.nbdeyy.constant.MessageConstant;
import com.nbdeyy.result.Result;
import com.nbdeyy.service.RSAKeyService;
import com.nbdeyy.utils.AesUtil;
import com.nbdeyy.utils.RsaUtil;
import com.nbdeyy.wrapper.RequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 *  替换请求体的过滤逻辑
 *
 **/
@Slf4j
public class ReplaceStreamFilter implements Filter {

    private RSAKeyService rsaKeyService;

    public ReplaceStreamFilter(RSAKeyService rsaKeyService){ this.rsaKeyService = rsaKeyService;}


    // 不进行解密的url
    private static final List<String> EXCLUDE_URLS = Arrays.asList(
            "/common/avatar/upload", // 头像上传路径
            "/user/user/captcha/verify" // 验证码验证调用
    );

    // 加密后的AES秘钥
    private static final String AES_KEY = "aesKey";
    // 加密后的数据
    private static final String DATA = "content";
    // 签名
    private static final String SIGNATURE = "signature";
    // 客户端公钥
    private static final String CLIENT_PUBLIC_KEY = "clientPublicKey";
    // 客户端时间戳
    private static final String TIMESTAMP = "t";

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();
        log.info("请求路径：{}" , requestURI);

        // 1. 白名单路径放行
        if (isExcludeUrl(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. 先转换自己的wrapper，实现多次读写
        RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest) request);

        // 2.1 然后判断GET请求不加密
        if (requestWrapper.getMethod().equals("GET")) {
            log.info("本次请求为GET请求，放行");
            chain.doFilter(requestWrapper, response);
            return;
        }

        // 2.2 Feign调用，内部请求，按照不加密的逻辑放行
        // 前端 --> A  -->  B  -->  C
        if (Objects.equals(requestWrapper.getHeader(FeignConstant.SOURCE_KEY), FeignConstant.SOURCE_VALUE)) {
            chain.doFilter(requestWrapper, response);
            return;
        }


        // 读出json请求体
        StringBuffer buffer = new StringBuffer();
        String line = null;
        BufferedReader reader = null;
        reader = requestWrapper.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        String body = buffer.toString();
        if (body.isEmpty() || body.trim().isEmpty()) {
            log.warn("请求体为空，跳过解密");
            chain.doFilter(requestWrapper, response);
            return;
        }

        // 获取前端的数据
        JSONObject jsonObject = JSON.parseObject(buffer.toString());

        // 加密之后的AES的key
        String clientAesKey = jsonObject.getString(AES_KEY);
        // AES加密得到的密文
        String clientContent = jsonObject.getString(DATA);
        // 签名数据
        String clientSignature = jsonObject.getString(SIGNATURE);
        // 客户端公钥
        String clientPublicKey = jsonObject.getString(CLIENT_PUBLIC_KEY);
        // 时间戳
        Long clientTimestamp = Long.valueOf(jsonObject.getString(TIMESTAMP));

        // 3. 验证签名
        boolean isValidSignature = RsaUtil.verify(clientTimestamp.toString(),clientPublicKey,clientContent,clientSignature);

        if (!isValidSignature) {
            // 检查签名失败
            log.warn("签名验证失败，请检查");
            handleVerificationFailure(httpResponse,MessageConstant.SIGNATURE_VERIFY_FAIL);
            return;
        }


        // 4.检查时间戳（防重放）
        if (System.currentTimeMillis() - clientTimestamp > 300000) { // 5分钟超时
            handleVerificationFailure(httpResponse,MessageConstant.TIME_OUT);
            return;
        }

        // 5. 继续解密数据

        // 先解密出aesKey
        clientAesKey = RsaUtil.decrypt(clientAesKey,rsaKeyService.getCurrentPrivateKey());
        // 再通过AES密钥解密数据
        String realData = AesUtil.decryptFromBase64(clientContent , clientAesKey);

        // 把解密之后的aesKey一并交给下游，方便在AOP对出参加密(暂时没实现)
        // 也可以把aesKey放在threadLocal中
        jsonObject = JSON.parseObject(realData);
        jsonObject.put(AES_KEY , clientAesKey);

        //重置json请求体，保证下游业务无感知获取数据
        //这一步只能在filter中完成，无法在aop或者是拦截器中实现
        // 设置新的请求体
        requestWrapper.setBody(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
        chain.doFilter(requestWrapper, response);
    }
 
    @Override
    public void destroy() {

    }

    /**
     * 判断是否为需要解密的url
     * @param requestURI
     * @return
     */
    private boolean isExcludeUrl(String requestURI) {
        return EXCLUDE_URLS.stream().anyMatch(url -> requestURI.startsWith(url));
    }

    private void handleVerificationFailure(HttpServletResponse response,String errorMessage) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        // 构建错误响应
        Result error = Result.error(errorMessage);
        response.getWriter().write(error.toJSONString());

        response.getWriter().flush();
    }
}