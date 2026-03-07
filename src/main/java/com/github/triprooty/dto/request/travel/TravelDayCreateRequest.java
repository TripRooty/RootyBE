package com.github.triprooty.dto.request.travel;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TravelDayCreateRequest {

    @NotNull(message = "orderNo는 필수입니다.")
    private Integer orderNo;

    @NotNull(message = "travelDate는 필수입니다.")
    private LocalDate travelDate;
}
