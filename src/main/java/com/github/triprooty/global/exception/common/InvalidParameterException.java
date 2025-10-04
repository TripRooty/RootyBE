package com.github.triprooty.global.exception.common;


import com.github.triprooty.global.exception.AppException;

public class InvalidParameterException extends AppException {
    public InvalidParameterException(String message) {
        super(CommonErrorCode.INVALID_PARAMETER);
    }
}