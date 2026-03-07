package com.github.triprooty.dto.request.travel;

import com.github.triprooty.domain.enums.TravelVisibility;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TravelUpdateRequest {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private TravelVisibility visibility;
}
