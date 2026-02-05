package com.pm.exceptions;

public class StockServiceException extends RuntimeException {
    private final String errorCode;
    private final int httpStatus;

    // Constructor with message, error code, and HTTP status
    public StockServiceException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    // Constructor with message, error code, HTTP status, and cause
    public StockServiceException(String message, String errorCode, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    // Getters for errorCode and httpStatus
    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
