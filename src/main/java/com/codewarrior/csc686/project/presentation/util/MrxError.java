package com.codewarrior.csc686.project.presentation.util;

public class MrxError {

    private String errorCode;
    private String errorMessage;

    public MrxError(String errorCode, String errorMessage) {

        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() { return errorCode; }

    public String getErrorMessage() { return errorMessage; }
}
