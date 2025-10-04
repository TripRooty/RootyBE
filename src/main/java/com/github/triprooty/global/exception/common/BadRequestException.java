package com.github.triprooty.global.exception.common;

import com.github.triprooty.global.exception.AppException;

public class BadRequestException extends AppException {
    public BadRequestException(String message) {
        super(CommonErrorCode.BAD_REQUEST);
    }
}
