package com.github.triprooty.controller.docs;

import com.github.triprooty.dto.request.travel.AddMoveItemRequest;
import com.github.triprooty.dto.request.travel.AddPlaceItemRequest;
import com.github.triprooty.dto.request.travel.ReorderItineraryItemsRequest;
import com.github.triprooty.dto.response.travel.ItineraryItemResponse;
import com.github.triprooty.global.dto.DataResponse;
import com.github.triprooty.global.dto.ErrorResponse;
import com.github.triprooty.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "TravelDay", description = "여행 day itinerary API")
public interface TravelDaySwaggerSpec {

    @Operation(summary = "day itinerary 조회", description = "travel day의 itinerary item 목록을 orderNo 기준으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "day 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<List<ItineraryItemResponse>>> getTravelDayItems(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelDayId
    );

    @Operation(summary = "장소 item 추가", description = "PLACE 타입 itinerary item을 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "day 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<ItineraryItemResponse>> addPlaceItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelDayId,
            @Valid @RequestBody AddPlaceItemRequest request
    );

    @Operation(summary = "이동 item 추가", description = "MOVE 타입 itinerary item을 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "day/location 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<ItineraryItemResponse>> addMoveItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelDayId,
            @Valid @RequestBody AddMoveItemRequest request
    );

    @Operation(summary = "itinerary 순서 변경", description = "day 내부 item orderNo를 일괄 재정렬합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "순서 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "day 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<List<ItineraryItemResponse>>> reorderItems(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelDayId,
            @Valid @RequestBody ReorderItineraryItemsRequest request
    );
}
