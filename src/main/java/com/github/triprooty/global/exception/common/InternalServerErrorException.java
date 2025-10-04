package com.github.triprooty.global.exception.common;

import com.github.triprooty.global.exception.AppException;

public class InternalServerErrorException extends AppException {
    public InternalServerErrorException(String message) {
        super(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }
}
