package com.github.triprooty.repository.travel;

import com.github.triprooty.domain.TravelParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TravelParticipantRepository extends JpaRepository<TravelParticipant, UUID> {
    boolean existsByTravelIdAndUserId(UUID travelId, UUID userId);
    Optional<TravelParticipant> findByTravelIdAndUserId(UUID travelId, UUID userId);
}
