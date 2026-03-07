package com.github.triprooty.global.exception.travel;

public class LocationNotFoundException extends TravelException {
    public LocationNotFoundException() {
        super(TravelErrorCode.LOCATION_NOT_FOUND);
    }
}
