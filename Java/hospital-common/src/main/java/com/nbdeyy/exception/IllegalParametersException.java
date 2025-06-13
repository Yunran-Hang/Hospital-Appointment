package com.nbdeyy.exception;

/**
 * 参数非法异常
 */
public class IllegalParametersException extends BaseException{
    public IllegalParametersException(){}
    public IllegalParametersException(String msg){
        super(msg);
    }
}
