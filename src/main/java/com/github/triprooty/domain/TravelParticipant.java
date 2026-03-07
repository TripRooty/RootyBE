package com.github.triprooty.domain;

import com.github.triprooty.domain.common.BaseEntity;
import com.github.triprooty.domain.enums.TravelParticipantRole;
import com.github.triprooty.domain.enums.TravelParticipantStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "travel_participants",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_travel_participant_travel_user", columnNames = {"travel_id", "user_id"})
        }
)
public class TravelParticipant extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "travel_participant_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private TravelParticipantRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TravelParticipantStatus status;

    @Builder
    public TravelParticipant(Travel travel, User user, TravelParticipantRole role, TravelParticipantStatus status) {
        this.travel = travel;
        this.user = user;
        this.role = role;
        this.status = status;
    }
}
