package com.github.triprooty.dto.response.travel;

import com.github.triprooty.domain.Travel;
import com.github.triprooty.domain.enums.TravelVisibility;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class TravelDetailResponse {
    private UUID travelId;
    private UUID ownerId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private TravelVisibility visibility;
    private List<TravelDayResponse> days;

    public static TravelDetailResponse from(Travel travel, List<TravelDayResponse> days) {
        return TravelDetailResponse.builder()
                .travelId(travel.getId())
                .ownerId(travel.getOwner().getId())
                .title(travel.getTitle())
                .description(travel.getDescription())
                .startDate(travel.getStartDate())
                .endDate(travel.getEndDate())
                .visibility(travel.getVisibility())
                .days(days)
                .build();
    }
}
