package com.github.triprooty.dto.response.travel;

import com.github.triprooty.domain.Location;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class LocationResponse {
    private UUID locationId;
    private String googlePlaceId;
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public static LocationResponse from(Location location) {
        return LocationResponse.builder()
                .locationId(location.getId())
                .googlePlaceId(location.getGooglePlaceId())
                .name(location.getName())
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }
}
