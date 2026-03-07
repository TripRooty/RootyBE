package com.github.triprooty.domain;

import com.github.triprooty.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "travel_scraps",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_travel_scrap_travel_user", columnNames = {"travel_id", "user_id"})
        }
)
public class TravelScrap extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "travel_scrap_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public TravelScrap(Travel travel, User user) {
        this.travel = travel;
        this.user = user;
    }
}
