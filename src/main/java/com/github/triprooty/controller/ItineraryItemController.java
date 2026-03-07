package com.github.triprooty.controller;

import com.github.triprooty.controller.docs.ItineraryItemSwaggerSpec;
import com.github.triprooty.dto.request.travel.UpdateMoveItemRequest;
import com.github.triprooty.dto.request.travel.UpdatePlaceItemRequest;
import com.github.triprooty.dto.response.travel.ItineraryItemResponse;
import com.github.triprooty.global.dto.DataResponse;
import com.github.triprooty.global.security.UserPrincipal;
import com.github.triprooty.service.travel.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/itinerary-items")
public class ItineraryItemController implements ItineraryItemSwaggerSpec {

    private final ItineraryService itineraryService;

    @PatchMapping("/{itemId}/place")
    public ResponseEntity<DataResponse<ItineraryItemResponse>> updatePlaceItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID itemId,
            @RequestBody UpdatePlaceItemRequest request
    ) {
        return ResponseEntity.ok(DataResponse.from(itineraryService.updatePlaceItem(userPrincipal.getId(), itemId, request)));
    }

    @PatchMapping("/{itemId}/move")
    public ResponseEntity<DataResponse<ItineraryItemResponse>> updateMoveItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID itemId,
            @RequestBody UpdateMoveItemRequest request
    ) {
        return ResponseEntity.ok(DataResponse.from(itineraryService.updateMoveItem(userPrincipal.getId(), itemId, request)));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<DataResponse<Void>> deleteItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID itemId
    ) {
        itineraryService.deleteItem(userPrincipal.getId(), itemId);
        return ResponseEntity.ok(DataResponse.ok());
    }
}
