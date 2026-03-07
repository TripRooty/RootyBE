package com.github.triprooty.dto.response.travel;

import com.github.triprooty.domain.PlaceVisit;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceVisitResponse {
    private LocationResponse location;
    private Integer stayMinutes;
    private String note;

    public static PlaceVisitResponse from(PlaceVisit placeVisit) {
        return PlaceVisitResponse.builder()
                .location(LocationResponse.from(placeVisit.getLocation()))
                .stayMinutes(placeVisit.getStayMinutes())
                .note(placeVisit.getNote())
                .build();
    }
}
