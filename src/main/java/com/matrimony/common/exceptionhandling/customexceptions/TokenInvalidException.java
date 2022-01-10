package com.matrimony.common.exceptionhandling.customexceptions;

public class TokenInvalidException extends RuntimeException{
    private String msg;

    public TokenInvalidException(String msg) {
        super(msg);
    }
}
