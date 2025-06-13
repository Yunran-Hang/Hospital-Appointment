package com.nbdeyy.exception;

public class DuplicatePasswordException extends BaseException{
    public DuplicatePasswordException() {}
    public DuplicatePasswordException(String msg){
        super(msg);
    }
}
