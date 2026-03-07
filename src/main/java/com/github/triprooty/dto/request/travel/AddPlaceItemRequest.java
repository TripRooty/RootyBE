package com.github.triprooty.dto.request.travel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AddPlaceItemRequest {

    @NotNull(message = "orderNo는 필수입니다.")
    private Integer orderNo;

    private String memo;

    @NotBlank(message = "googlePlaceId는 필수입니다.")
    private String googlePlaceId;

    @NotBlank(message = "name은 필수입니다.")
    private String name;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer stayMinutes;

    private String note;
}
