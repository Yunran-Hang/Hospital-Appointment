package com.nbdeyy.exception;

/**
 * 医生删除异常
 */
public class DoctorDeleteException extends BaseException{
    public DoctorDeleteException() {}

    public DoctorDeleteException(String msg){
        super(msg);
    }
}
