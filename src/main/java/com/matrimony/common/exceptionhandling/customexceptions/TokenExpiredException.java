package com.matrimony.common.exceptionhandling.customexceptions;

public class TokenExpiredException extends RuntimeException{
    private String msg;

    public TokenExpiredException(String msg) {
        super(msg);
    }
}
