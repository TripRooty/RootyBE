package com.github.triprooty.dto.request.travel;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class ReorderItineraryItemsRequest {

    @NotEmpty(message = "itemIds는 비어 있을 수 없습니다.")
    private List<UUID> itemIds;
}
