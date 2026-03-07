package com.github.triprooty.service.travel;

import com.github.triprooty.domain.Location;
import com.github.triprooty.domain.RouteCache;
import com.github.triprooty.domain.enums.RouteSource;
import com.github.triprooty.domain.enums.TransportType;
import com.github.triprooty.global.exception.travel.LocationNotFoundException;
import com.github.triprooty.repository.travel.LocationRepository;
import com.github.triprooty.repository.travel.RouteCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouteCacheDomainService {

    private final RouteCacheRepository routeCacheRepository;
    private final LocationRepository locationRepository;

    public RouteCache getRouteCache(UUID fromLocationId, UUID toLocationId, TransportType transportType) {
        return routeCacheRepository.findByFromLocationIdAndToLocationIdAndTransportType(fromLocationId, toLocationId, transportType)
                .orElse(null);
    }

    @Transactional
    public RouteCache getOrResolve(UUID fromLocationId, UUID toLocationId, TransportType transportType) {
        return routeCacheRepository.findByFromLocationIdAndToLocationIdAndTransportType(fromLocationId, toLocationId, transportType)
                .orElseGet(() -> resolveAndSave(fromLocationId, toLocationId, transportType));
    }

    @Transactional
    public RouteCache resolveAndSave(UUID fromLocationId, UUID toLocationId, TransportType transportType) {
        Location fromLocation = locationRepository.findById(fromLocationId)
                .orElseThrow(LocationNotFoundException::new);
        Location toLocation = locationRepository.findById(toLocationId)
                .orElseThrow(LocationNotFoundException::new);

        // TODO: 실제 Google Route API 연동 (캐시 미스일 때만 호출)
        RouteEstimate estimate = estimateFallback(transportType);

        RouteCache cache = RouteCache.builder()
                .fromLocation(fromLocation)
                .toLocation(toLocation)
                .transportType(transportType)
                .routeSource(RouteSource.MANUAL)
                .distanceMeters(estimate.distanceMeters())
                .durationSeconds(estimate.durationSeconds())
                .polyline(estimate.polyline())
                .rawResponse(null)
                .build();

        return routeCacheRepository.save(cache);
    }

    private RouteEstimate estimateFallback(TransportType transportType) {
        return switch (transportType) {
            case WALK -> new RouteEstimate(1200, 900, null);
            case BICYCLE -> new RouteEstimate(2500, 700, null);
            case TRANSIT -> new RouteEstimate(4000, 1100, null);
            case DRIVE -> new RouteEstimate(5000, 600, null);
        };
    }

    private record RouteEstimate(int distanceMeters, int durationSeconds, String polyline) {
    }
}
