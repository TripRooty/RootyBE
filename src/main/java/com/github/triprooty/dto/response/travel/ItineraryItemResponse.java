package com.github.triprooty.dto.response.travel;

import com.github.triprooty.domain.ItineraryItem;
import com.github.triprooty.domain.enums.ItineraryItemType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ItineraryItemResponse {
    private UUID itemId;
    private Integer orderNo;
    private ItineraryItemType itemType;
    private String memo;
    private PlaceVisitResponse placeVisit;
    private MoveSegmentResponse moveSegment;

    public static ItineraryItemResponse from(ItineraryItem itineraryItem) {
        return ItineraryItemResponse.builder()
                .itemId(itineraryItem.getId())
                .orderNo(itineraryItem.getOrderNo())
                .itemType(itineraryItem.getItemType())
                .memo(itineraryItem.getMemo())
                .placeVisit(itineraryItem.getPlaceVisit() == null ? null : PlaceVisitResponse.from(itineraryItem.getPlaceVisit()))
                .moveSegment(itineraryItem.getMoveSegment() == null ? null : MoveSegmentResponse.from(itineraryItem.getMoveSegment()))
                .build();
    }
}
