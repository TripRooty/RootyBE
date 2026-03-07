package com.github.triprooty.controller;

import com.github.triprooty.controller.docs.TravelDaySwaggerSpec;
import com.github.triprooty.dto.request.travel.AddMoveItemRequest;
import com.github.triprooty.dto.request.travel.AddPlaceItemRequest;
import com.github.triprooty.dto.request.travel.ReorderItineraryItemsRequest;
import com.github.triprooty.dto.response.travel.ItineraryItemResponse;
import com.github.triprooty.global.dto.DataResponse;
import com.github.triprooty.global.security.UserPrincipal;
import com.github.triprooty.service.travel.ItineraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/travel-days")
public class TravelDayController implements TravelDaySwaggerSpec {

    private final ItineraryService itineraryService;

    @GetMapping("/{travelDayId}/items")
    public ResponseEntity<DataResponse<List<ItineraryItemResponse>>> getTravelDayItems(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelDayId
    ) {
        return ResponseEntity.ok(DataResponse.from(itineraryService.getDayItems(userPrincipal.getId(), travelDayId)));
    }

    @PostMapping("/{travelDayId}/items/places")
    public ResponseEntity<DataResponse<ItineraryItemResponse>> addPlaceItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelDayId,
            @Valid @RequestBody AddPlaceItemRequest request
    ) {
        return ResponseEntity.ok(DataResponse.from(itineraryService.addPlaceItem(userPrincipal.getId(), travelDayId, request)));
    }

    @PostMapping("/{travelDayId}/items/moves")
    public ResponseEntity<DataResponse<ItineraryItemResponse>> addMoveItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelDayId,
            @Valid @RequestBody AddMoveItemRequest request
    ) {
        return ResponseEntity.ok(DataResponse.from(itineraryService.addMoveItem(userPrincipal.getId(), travelDayId, request)));
    }

    @PatchMapping("/{travelDayId}/items/order")
    public ResponseEntity<DataResponse<List<ItineraryItemResponse>>> reorderItems(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelDayId,
            @Valid @RequestBody ReorderItineraryItemsRequest request
    ) {
        return ResponseEntity.ok(DataResponse.from(itineraryService.reorderItems(userPrincipal.getId(), travelDayId, request)));
    }
}
