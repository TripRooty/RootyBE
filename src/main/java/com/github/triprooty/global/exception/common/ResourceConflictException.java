package com.github.triprooty.global.exception.common;

import com.github.triprooty.global.exception.AppException;

public class ResourceConflictException extends AppException {
    public ResourceConflictException(String message) {
        super(CommonErrorCode.RESOURCE_CONFLICT);
    }
}
