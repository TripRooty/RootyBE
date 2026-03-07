package com.github.triprooty.global.exception.travel;

public class ItineraryItemNotFoundException extends TravelException {
    public ItineraryItemNotFoundException() {
        super(TravelErrorCode.ITINERARY_ITEM_NOT_FOUND);
    }
}
