package com.github.triprooty.dto.request.travel;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdatePlaceItemRequest {
    private String memo;
    private String googlePlaceId;
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer stayMinutes;
    private String note;
}
