package com.github.triprooty.dto.response.travel;

import com.github.triprooty.domain.RouteCache;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class RouteCacheResponse {
    private UUID routeCacheId;
    private UUID fromLocationId;
    private UUID toLocationId;
    private String transportType;
    private String routeSource;
    private Integer distanceMeters;
    private Integer durationSeconds;
    private String polyline;

    public static RouteCacheResponse from(RouteCache routeCache) {
        return RouteCacheResponse.builder()
                .routeCacheId(routeCache.getId())
                .fromLocationId(routeCache.getFromLocation().getId())
                .toLocationId(routeCache.getToLocation().getId())
                .transportType(routeCache.getTransportType().name())
                .routeSource(routeCache.getRouteSource().name())
                .distanceMeters(routeCache.getDistanceMeters())
                .durationSeconds(routeCache.getDurationSeconds())
                .polyline(routeCache.getPolyline())
                .build();
    }
}
