package com.github.triprooty.domain;

import com.github.triprooty.domain.common.BaseEntity;
import com.github.triprooty.domain.enums.RouteSource;
import com.github.triprooty.domain.enums.TransportType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "route_cache",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_route_cache_from_to_transport", columnNames = {"from_location_id", "to_location_id", "transport_type"})
        }
)
public class RouteCache extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "route_cache_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_location_id", nullable = false)
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_location_id", nullable = false)
    private Location toLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type", nullable = false, length = 30)
    private TransportType transportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "route_source", nullable = false, length = 30)
    private RouteSource routeSource;

    @Column(name = "distance_meters", nullable = false)
    private Integer distanceMeters;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    @Column(name = "polyline", columnDefinition = "TEXT")
    private String polyline;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Builder
    public RouteCache(Location fromLocation,
                      Location toLocation,
                      TransportType transportType,
                      RouteSource routeSource,
                      Integer distanceMeters,
                      Integer durationSeconds,
                      String polyline,
                      String rawResponse) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.transportType = transportType;
        this.routeSource = routeSource;
        this.distanceMeters = distanceMeters;
        this.durationSeconds = durationSeconds;
        this.polyline = polyline;
        this.rawResponse = rawResponse;
    }

    public void refresh(RouteSource routeSource,
                        Integer distanceMeters,
                        Integer durationSeconds,
                        String polyline,
                        String rawResponse) {
        this.routeSource = routeSource;
        this.distanceMeters = distanceMeters;
        this.durationSeconds = durationSeconds;
        this.polyline = polyline;
        this.rawResponse = rawResponse;
    }
}
