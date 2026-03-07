package com.github.triprooty.domain;

import com.github.triprooty.domain.common.BaseEntity;
import com.github.triprooty.domain.enums.TransportType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "move_segments")
public class MoveSegment extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "move_segment_id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "itinerary_item_id", nullable = false, unique = true)
    private ItineraryItem itineraryItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_location_id", nullable = false)
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_location_id", nullable = false)
    private Location toLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_cache_id")
    private RouteCache routeCache;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type", nullable = false, length = 30)
    private TransportType transportType;

    @Column(name = "distance_meters")
    private Integer distanceMeters;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "polyline", columnDefinition = "TEXT")
    private String polyline;

    @Builder
    public MoveSegment(ItineraryItem itineraryItem,
                       Location fromLocation,
                       Location toLocation,
                       RouteCache routeCache,
                       TransportType transportType,
                       Integer distanceMeters,
                       Integer durationSeconds,
                       String polyline) {
        this.itineraryItem = itineraryItem;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.routeCache = routeCache;
        this.transportType = transportType;
        this.distanceMeters = distanceMeters;
        this.durationSeconds = durationSeconds;
        this.polyline = polyline;
    }

    public void update(Location fromLocation,
                       Location toLocation,
                       RouteCache routeCache,
                       TransportType transportType,
                       Integer distanceMeters,
                       Integer durationSeconds,
                       String polyline) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.routeCache = routeCache;
        this.transportType = transportType;
        this.distanceMeters = distanceMeters;
        this.durationSeconds = durationSeconds;
        this.polyline = polyline;
    }
}
