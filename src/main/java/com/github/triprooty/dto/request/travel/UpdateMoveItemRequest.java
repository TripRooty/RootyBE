package com.github.triprooty.dto.request.travel;

import com.github.triprooty.domain.enums.TransportType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateMoveItemRequest {
    private String memo;
    private UUID fromLocationId;
    private UUID toLocationId;
    private TransportType transportType;
}
