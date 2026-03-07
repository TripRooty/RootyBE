package com.github.triprooty.domain;

import com.github.triprooty.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "place_visits")
public class PlaceVisit extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "place_visit_id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "itinerary_item_id", nullable = false, unique = true)
    private ItineraryItem itineraryItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "stay_minutes")
    private Integer stayMinutes;

    @Column(name = "note", length = 1000)
    private String note;

    @Builder
    public PlaceVisit(ItineraryItem itineraryItem, Location location, Integer stayMinutes, String note) {
        this.itineraryItem = itineraryItem;
        this.location = location;
        this.stayMinutes = stayMinutes;
        this.note = note;
    }

    public void update(Location location, Integer stayMinutes, String note) {
        this.location = location;
        this.stayMinutes = stayMinutes;
        this.note = note;
    }
}
