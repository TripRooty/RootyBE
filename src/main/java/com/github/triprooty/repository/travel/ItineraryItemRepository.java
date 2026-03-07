package com.github.triprooty.repository.travel;

import com.github.triprooty.domain.ItineraryItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItineraryItemRepository extends JpaRepository<ItineraryItem, UUID> {

    List<ItineraryItem> findByTravelDayIdOrderByOrderNoAsc(UUID travelDayId);

    @EntityGraph(attributePaths = {
            "placeVisit",
            "placeVisit.location",
            "moveSegment",
            "moveSegment.fromLocation",
            "moveSegment.toLocation"
    })
    List<ItineraryItem> findWithDetailByTravelDayIdOrderByOrderNoAsc(UUID travelDayId);

    @EntityGraph(attributePaths = {
            "travelDay",
            "travelDay.travel",
            "placeVisit",
            "placeVisit.location",
            "moveSegment",
            "moveSegment.fromLocation",
            "moveSegment.toLocation"
    })
    Optional<ItineraryItem> findWithDetailById(UUID id);
}
