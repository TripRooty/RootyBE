package com.github.triprooty.repository.travel;

import com.github.triprooty.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
    Optional<Location> findByGooglePlaceId(String googlePlaceId);
}
