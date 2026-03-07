package com.github.triprooty.dto.response.travel;

import com.github.triprooty.domain.MoveSegment;
import com.github.triprooty.domain.RouteCache;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class MoveSegmentResponse {
    private UUID routeCacheId;
    private LocationResponse fromLocation;
    private LocationResponse toLocation;
    private String transportType;
    private Integer distanceMeters;
    private Integer durationSeconds;
    private String polyline;

    public static MoveSegmentResponse from(MoveSegment moveSegment) {
        RouteCache routeCache = moveSegment.getRouteCache();
        return MoveSegmentResponse.builder()
                .routeCacheId(routeCache == null ? null : routeCache.getId())
                .fromLocation(LocationResponse.from(moveSegment.getFromLocation()))
                .toLocation(LocationResponse.from(moveSegment.getToLocation()))
                .transportType(moveSegment.getTransportType().name())
                .distanceMeters(moveSegment.getDistanceMeters())
                .durationSeconds(moveSegment.getDurationSeconds())
                .polyline(moveSegment.getPolyline())
                .build();
    }
}
