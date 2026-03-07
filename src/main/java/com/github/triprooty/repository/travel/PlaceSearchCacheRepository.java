package com.github.triprooty.repository.travel;

import com.github.triprooty.domain.PlaceSearchCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlaceSearchCacheRepository extends JpaRepository<PlaceSearchCache, UUID> {
    Optional<PlaceSearchCache> findByCacheKey(String cacheKey);
}
