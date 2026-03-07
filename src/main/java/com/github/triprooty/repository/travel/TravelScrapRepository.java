package com.github.triprooty.repository.travel;

import com.github.triprooty.domain.TravelScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TravelScrapRepository extends JpaRepository<TravelScrap, UUID> {
    Optional<TravelScrap> findByTravelIdAndUserId(UUID travelId, UUID userId);
}
