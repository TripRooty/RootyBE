package com.github.triprooty.repository.travel;

import com.github.triprooty.domain.MoveSegment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MoveSegmentRepository extends JpaRepository<MoveSegment, UUID> {
}
