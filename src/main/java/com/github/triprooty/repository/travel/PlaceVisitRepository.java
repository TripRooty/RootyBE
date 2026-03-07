package com.github.triprooty.repository.travel;

import com.github.triprooty.domain.PlaceVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaceVisitRepository extends JpaRepository<PlaceVisit, UUID> {
}
