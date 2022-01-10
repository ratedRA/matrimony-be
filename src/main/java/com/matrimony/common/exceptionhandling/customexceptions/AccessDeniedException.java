package com.matrimony.common.exceptionhandling.customexceptions;

public class AccessDeniedException extends RuntimeException{
    private String msg;

    public AccessDeniedException(String msg) {
        super(msg);
    }
}
