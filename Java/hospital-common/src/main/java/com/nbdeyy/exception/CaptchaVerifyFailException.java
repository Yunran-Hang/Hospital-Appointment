package com.nbdeyy.exception;

/**
 * 验证码验证失败异常
 */
public class CaptchaVerifyFailException extends BaseException{
    public CaptchaVerifyFailException() {
    }

    public CaptchaVerifyFailException(String msg) {
        super(msg);
    }
}
