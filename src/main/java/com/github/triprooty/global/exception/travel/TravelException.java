package com.github.triprooty.global.exception.travel;

import com.github.triprooty.global.exception.AppException;

public class TravelException extends AppException {
    public TravelException(TravelErrorCode errorCode) {
        super(errorCode);
    }
}
