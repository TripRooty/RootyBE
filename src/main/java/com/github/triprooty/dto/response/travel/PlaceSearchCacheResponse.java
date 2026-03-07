package com.github.triprooty.dto.response.travel;

import com.github.triprooty.domain.PlaceSearchCache;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PlaceSearchCacheResponse {
    private UUID placeSearchCacheId;
    private String cacheKey;
    private String queryText;
    private String responseJson;
    private LocalDateTime expiresAt;

    public static PlaceSearchCacheResponse from(PlaceSearchCache cache) {
        return PlaceSearchCacheResponse.builder()
                .placeSearchCacheId(cache.getId())
                .cacheKey(cache.getCacheKey())
                .queryText(cache.getQueryText())
                .responseJson(cache.getResponseJson())
                .expiresAt(cache.getExpiresAt())
                .build();
    }
}
