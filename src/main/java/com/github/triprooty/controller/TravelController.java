package com.github.triprooty.controller;

import com.github.triprooty.controller.docs.TravelSwaggerSpec;
import com.github.triprooty.dto.request.travel.TravelCreateRequest;
import com.github.triprooty.dto.request.travel.TravelDayCreateRequest;
import com.github.triprooty.dto.request.travel.TravelDayReorderRequest;
import com.github.triprooty.dto.request.travel.TravelUpdateRequest;
import com.github.triprooty.dto.response.travel.TravelDayResponse;
import com.github.triprooty.dto.response.travel.TravelDetailResponse;
import com.github.triprooty.dto.response.travel.TravelSummaryResponse;
import com.github.triprooty.global.dto.DataResponse;
import com.github.triprooty.global.security.UserPrincipal;
import com.github.triprooty.service.travel.TravelScrapService;
import com.github.triprooty.service.travel.TravelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/travels")
public class TravelController implements TravelSwaggerSpec {

    private final TravelService travelService;
    private final TravelScrapService travelScrapService;

    @GetMapping
    public ResponseEntity<DataResponse<List<TravelSummaryResponse>>> getTravels() {
        return ResponseEntity.ok(DataResponse.from(List.of()));
    }

    @PostMapping
    public ResponseEntity<DataResponse<TravelSummaryResponse>> createTravel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody TravelCreateRequest request
    ) {
        return ResponseEntity.ok(DataResponse.from(travelService.createTravel(userPrincipal.getId(), request)));
    }

    @GetMapping("/{travelId}")
    public ResponseEntity<DataResponse<TravelDetailResponse>> getTravelDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId
    ) {
        return ResponseEntity.ok(DataResponse.from(travelService.getTravelDetail(userPrincipal.getId(), travelId)));
    }

    @PatchMapping("/{travelId}")
    public ResponseEntity<DataResponse<Void>> updateTravel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId,
            @RequestBody TravelUpdateRequest request
    ) {
        travelService.updateTravel(userPrincipal.getId(), travelId, request);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @DeleteMapping("/{travelId}")
    public ResponseEntity<DataResponse<Void>> deleteTravel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId
    ) {
        travelService.deleteTravel(userPrincipal.getId(), travelId);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @GetMapping("/{travelId}/days")
    public ResponseEntity<DataResponse<List<TravelDayResponse>>> getTravelDays(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId
    ) {
        return ResponseEntity.ok(DataResponse.from(travelService.getTravelDays(userPrincipal.getId(), travelId)));
    }

    @PostMapping("/{travelId}/days")
    public ResponseEntity<DataResponse<TravelDayResponse>> createTravelDay(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId,
            @Valid @RequestBody TravelDayCreateRequest request
    ) {
        return ResponseEntity.ok(DataResponse.from(travelService.createTravelDay(userPrincipal.getId(), travelId, request)));
    }

    @PatchMapping("/{travelId}/days")
    public ResponseEntity<DataResponse<List<TravelDayResponse>>> reorderTravelDays(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId,
            @Valid @RequestBody TravelDayReorderRequest request
    ) {
        return ResponseEntity.ok(DataResponse.from(travelService.reorderTravelDays(userPrincipal.getId(), travelId, request)));
    }

    @PostMapping("/{travelId}/scrap")
    public ResponseEntity<DataResponse<Void>> scrapTravel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId
    ) {
        travelScrapService.scrap(userPrincipal.getId(), travelId);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @DeleteMapping("/{travelId}/scrap")
    public ResponseEntity<DataResponse<Void>> unscrapTravel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId
    ) {
        travelScrapService.unscrap(userPrincipal.getId(), travelId);
        return ResponseEntity.ok(DataResponse.ok());
    }
}
