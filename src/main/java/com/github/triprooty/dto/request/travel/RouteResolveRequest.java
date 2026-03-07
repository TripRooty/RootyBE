package com.github.triprooty.dto.request.travel;

import com.github.triprooty.domain.enums.TransportType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RouteResolveRequest {

    @NotNull(message = "fromLocationId는 필수입니다.")
    private UUID fromLocationId;

    @NotNull(message = "toLocationId는 필수입니다.")
    private UUID toLocationId;

    @NotNull(message = "transportType은 필수입니다.")
    private TransportType transportType;
}
