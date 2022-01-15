package com.matrimony.common.exceptionhandling.customexceptions;

public class OtpInvalidException extends RuntimeException {
    public OtpInvalidException(String message) {
        super(message);
    }
}
