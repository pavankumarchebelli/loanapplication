package com.nexa.audit.exception;

import org.springframework.http.HttpStatus;

public record ApiError(String code, String message) {
    public static ApiError of(HttpStatus status, String message) {
        return new ApiError(status.name(), message);
    }
}
