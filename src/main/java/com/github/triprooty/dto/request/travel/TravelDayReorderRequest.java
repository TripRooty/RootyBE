package com.github.triprooty.dto.request.travel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class TravelDayReorderRequest {

    @Valid
    @NotEmpty(message = "dayIds는 비어 있을 수 없습니다.")
    private List<UUID> dayIds;
}
