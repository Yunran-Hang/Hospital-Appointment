package com.nbdeyy.exception;

/**
 * 预约重复异常
 */
public class DuplicateAppointmentsException extends BaseException{
    public DuplicateAppointmentsException() {}

    public DuplicateAppointmentsException(String msg){
        super(msg);
    }
}
