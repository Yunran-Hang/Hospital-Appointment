package com.nbdeyy.exception;

/**
 * 账号已经存在异常
 */
public class AccountAlreadyExistException extends BaseException{
    public AccountAlreadyExistException() {}

    public AccountAlreadyExistException(String msg){
        super(msg);
    }
}
