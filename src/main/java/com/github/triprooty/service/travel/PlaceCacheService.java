package com.github.triprooty.service.travel;

import com.github.triprooty.domain.Location;
import com.github.triprooty.domain.PlaceSearchCache;
import com.github.triprooty.dto.response.travel.PlaceSearchCacheResponse;
import com.github.triprooty.repository.travel.LocationRepository;
import com.github.triprooty.repository.travel.PlaceSearchCacheRepository;
import com.github.triprooty.service.external.GooglePlacesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceCacheService {

    private final PlaceSearchCacheRepository placeSearchCacheRepository;
    private final LocationRepository locationRepository;
    private final GooglePlacesService googlePlacesService;

    @Transactional
    public PlaceSearchCacheResponse getCacheSearch(String query) {
        String cacheKey = normalizeQuery(query);

        PlaceSearchCache cache = placeSearchCacheRepository.findByCacheKey(cacheKey)
                .filter(c -> !c.isExpired(LocalDateTime.now()))
                .orElseGet(() -> searchFromProviderAndCache(query, cacheKey));

        return PlaceSearchCacheResponse.from(cache);
    }

    private PlaceSearchCache searchFromProviderAndCache(String query, String cacheKey) {
        GooglePlacesService.PlaceSearchResult result = googlePlacesService.searchText(query);
        String responseJson = result.responseJson();

        result.candidates().forEach(this::upsertLocation);

        PlaceSearchCache cache = placeSearchCacheRepository.findByCacheKey(cacheKey)
                .orElseGet(() -> PlaceSearchCache.builder()
                        .cacheKey(cacheKey)
                        .queryText(query)
                        .responseJson(responseJson)
                        .expiresAt(LocalDateTime.now().plusHours(6))
                        .build());

        cache.refresh(responseJson, LocalDateTime.now().plusHours(6));
        return placeSearchCacheRepository.save(cache);
    }

    private String normalizeQuery(String query) {
        return query == null ? "" : query.trim().toLowerCase();
    }

    private void upsertLocation(GooglePlacesService.PlaceCandidate candidate) {
        locationRepository.findByGooglePlaceId(candidate.googlePlaceId())
                .map(location -> {
                    location.refresh(
                            candidate.name(),
                            candidate.address(),
                            candidate.latitude(),
                            candidate.longitude()
                    );
                    return location;
                })
                .orElseGet(() -> locationRepository.save(
                        Location.builder()
                                .googlePlaceId(candidate.googlePlaceId())
                                .name(candidate.name())
                                .address(candidate.address())
                                .latitude(candidate.latitude())
                                .longitude(candidate.longitude())
                                .build()
                ));
    }
}
