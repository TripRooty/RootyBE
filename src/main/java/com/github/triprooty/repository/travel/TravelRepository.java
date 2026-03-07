package com.github.triprooty.repository.travel;

import com.github.triprooty.domain.Travel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TravelRepository extends JpaRepository<Travel, UUID> {

    @EntityGraph(attributePaths = {"owner"})
    Optional<Travel> findWithOwnerById(UUID id);
}
