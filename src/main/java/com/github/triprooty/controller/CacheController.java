package com.github.triprooty.controller;

import com.github.triprooty.controller.docs.CacheSwaggerSpec;
import com.github.triprooty.domain.RouteCache;
import com.github.triprooty.dto.request.travel.RouteResolveRequest;
import com.github.triprooty.dto.response.travel.PlaceSearchCacheResponse;
import com.github.triprooty.dto.response.travel.RouteCacheResponse;
import com.github.triprooty.global.dto.DataResponse;
import com.github.triprooty.service.travel.PlaceCacheService;
import com.github.triprooty.service.travel.RouteCacheDomainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CacheController implements CacheSwaggerSpec {

    private final PlaceCacheService placeCacheService;
    private final RouteCacheDomainService routeCacheDomainService;

    @GetMapping("/places/cache-search")
    public ResponseEntity<DataResponse<PlaceSearchCacheResponse>> cacheSearch(@RequestParam String query) {
        return ResponseEntity.ok(DataResponse.from(placeCacheService.getCacheSearch(query)));
    }

    @GetMapping("/routes/cache")
    public ResponseEntity<DataResponse<RouteCacheResponse>> getRouteCache(
            @RequestParam UUID fromLocationId,
            @RequestParam UUID toLocationId,
            @RequestParam String transportType
    ) {
        RouteCache routeCache = routeCacheDomainService.getRouteCache(
                fromLocationId,
                toLocationId,
                com.github.triprooty.domain.enums.TransportType.valueOf(transportType)
        );
        return ResponseEntity.ok(DataResponse.from(routeCache == null ? null : RouteCacheResponse.from(routeCache)));
    }

    @PostMapping("/routes/resolve")
    public ResponseEntity<DataResponse<RouteCacheResponse>> resolveRoute(
            @Valid @RequestBody RouteResolveRequest request
    ) {
        RouteCache routeCache = routeCacheDomainService.getOrResolve(
                request.getFromLocationId(),
                request.getToLocationId(),
                request.getTransportType()
        );
        return ResponseEntity.ok(DataResponse.from(RouteCacheResponse.from(routeCache)));
    }
}
