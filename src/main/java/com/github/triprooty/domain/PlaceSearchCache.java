package com.github.triprooty.domain;

import com.github.triprooty.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "place_search_cache",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_place_search_cache_key", columnNames = {"cache_key"})
        },
        indexes = {
                @Index(name = "idx_place_search_cache_key", columnList = "cache_key"),
                @Index(name = "idx_place_search_cache_expires_at", columnList = "expires_at")
        }
)
public class PlaceSearchCache extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "place_search_cache_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "cache_key", nullable = false, length = 255)
    private String cacheKey;

    @Column(name = "query_text", nullable = false, length = 500)
    private String queryText;

    @Column(name = "response_json", nullable = false, columnDefinition = "TEXT")
    private String responseJson;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Builder
    public PlaceSearchCache(String cacheKey, String queryText, String responseJson, LocalDateTime expiresAt) {
        this.cacheKey = cacheKey;
        this.queryText = queryText;
        this.responseJson = responseJson;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired(LocalDateTime now) {
        return expiresAt.isBefore(now);
    }

    public void refresh(String responseJson, LocalDateTime expiresAt) {
        this.responseJson = responseJson;
        this.expiresAt = expiresAt;
    }
}
