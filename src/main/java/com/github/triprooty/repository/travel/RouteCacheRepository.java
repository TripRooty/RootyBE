package com.github.triprooty.repository.travel;

import com.github.triprooty.domain.RouteCache;
import com.github.triprooty.domain.enums.TransportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RouteCacheRepository extends JpaRepository<RouteCache, UUID> {
    Optional<RouteCache> findByFromLocationIdAndToLocationIdAndTransportType(
            UUID fromLocationId,
            UUID toLocationId,
            TransportType transportType
    );
}
