package com.github.triprooty.global.exception.common;

import com.github.triprooty.global.exception.AppException;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(CommonErrorCode.NOT_FOUND);
    }
}
