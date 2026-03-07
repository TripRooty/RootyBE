package com.github.triprooty.dto.response.travel;

import com.github.triprooty.domain.TravelDay;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class TravelDayResponse {
    private UUID travelDayId;
    private Integer orderNo;
    private LocalDate travelDate;
    private List<ItineraryItemResponse> items;

    public static TravelDayResponse from(TravelDay travelDay, List<ItineraryItemResponse> items) {
        return TravelDayResponse.builder()
                .travelDayId(travelDay.getId())
                .orderNo(travelDay.getOrderNo())
                .travelDate(travelDay.getTravelDate())
                .items(items)
                .build();
    }
}
