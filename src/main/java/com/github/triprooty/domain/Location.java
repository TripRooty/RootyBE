package com.github.triprooty.domain;

import com.github.triprooty.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "locations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_location_google_place_id", columnNames = {"google_place_id"})
        },
        indexes = {
                @Index(name = "idx_location_google_place_id", columnList = "google_place_id")
        }
)
public class Location extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "location_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "google_place_id", nullable = false, length = 255)
    private String googlePlaceId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Builder
    public Location(String googlePlaceId, String name, String address, BigDecimal latitude, BigDecimal longitude) {
        this.googlePlaceId = googlePlaceId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void refresh(String name, String address, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
