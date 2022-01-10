package com.matrimony.common.exceptionhandling.customexceptions;

public class DuplicateUserException extends RuntimeException{
    private String msg;

    public DuplicateUserException(String msg) {
        super(msg);
    }
}
