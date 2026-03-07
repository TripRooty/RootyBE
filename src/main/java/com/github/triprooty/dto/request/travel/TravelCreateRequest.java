package com.github.triprooty.dto.request.travel;

import com.github.triprooty.domain.enums.TravelVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TravelCreateRequest {

    @NotBlank(message = "title은 필수입니다.")
    private String title;

    private String description;

    @NotNull(message = "startDate는 필수입니다.")
    private LocalDate startDate;

    @NotNull(message = "endDate는 필수입니다.")
    private LocalDate endDate;

    @NotNull(message = "visibility는 필수입니다.")
    private TravelVisibility visibility;
}
