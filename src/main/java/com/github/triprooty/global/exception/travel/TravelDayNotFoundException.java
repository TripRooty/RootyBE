package com.github.triprooty.global.exception.travel;

public class TravelDayNotFoundException extends TravelException {
    public TravelDayNotFoundException() {
        super(TravelErrorCode.TRAVEL_DAY_NOT_FOUND);
    }
}
