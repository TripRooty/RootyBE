package com.github.triprooty.global.exception.travel;

public class TravelNotFoundException extends TravelException {
    public TravelNotFoundException() {
        super(TravelErrorCode.TRAVEL_NOT_FOUND);
    }
}
