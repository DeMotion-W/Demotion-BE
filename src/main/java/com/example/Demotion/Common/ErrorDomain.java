package com.example.Demotion.Common;

public class ErrorDomain extends RuntimeException{

    private final ErrorCode errorCode;

    public ErrorDomain(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public int getCode() {
        return errorCode.getCode();
    }

    public int getStatus() {
        return errorCode.getStatus();
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
