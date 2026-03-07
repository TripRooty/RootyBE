package com.github.triprooty.dto.response.travel;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class TravelSummaryResponse {
    private UUID travelId;
}
