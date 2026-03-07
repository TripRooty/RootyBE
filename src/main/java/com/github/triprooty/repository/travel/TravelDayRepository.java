package com.github.triprooty.repository.travel;

import com.github.triprooty.domain.TravelDay;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TravelDayRepository extends JpaRepository<TravelDay, UUID> {

    List<TravelDay> findByTravelIdOrderByOrderNoAsc(UUID travelId);

    @EntityGraph(attributePaths = {
            "itineraryItems",
            "itineraryItems.placeVisit",
            "itineraryItems.placeVisit.location",
            "itineraryItems.moveSegment",
            "itineraryItems.moveSegment.fromLocation",
            "itineraryItems.moveSegment.toLocation"
    })
    List<TravelDay> findWithItemsByTravelIdOrderByOrderNoAsc(UUID travelId);

    @EntityGraph(attributePaths = {
            "itineraryItems",
            "itineraryItems.placeVisit",
            "itineraryItems.placeVisit.location",
            "itineraryItems.moveSegment",
            "itineraryItems.moveSegment.fromLocation",
            "itineraryItems.moveSegment.toLocation"
    })
    Optional<TravelDay> findWithItemsById(UUID id);
}
