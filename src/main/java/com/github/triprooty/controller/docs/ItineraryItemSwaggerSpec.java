package com.github.triprooty.controller.docs;

import com.github.triprooty.dto.request.travel.UpdateMoveItemRequest;
import com.github.triprooty.dto.request.travel.UpdatePlaceItemRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "ItineraryItem", description = "itinerary item 수정/삭제 API")
public interface ItineraryItemSwaggerSpec {

    @Operation(summary = "장소 item 수정", description = "PLACE 타입 itinerary item 상세를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "타입 불일치 또는 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "item 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<ItineraryItemResponse>> updatePlaceItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID itemId,
            @RequestBody UpdatePlaceItemRequest request
    );

    @Operation(summary = "이동 item 수정", description = "MOVE 타입 itinerary item 상세를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "타입 불일치 또는 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "item 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<ItineraryItemResponse>> updateMoveItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID itemId,
            @RequestBody UpdateMoveItemRequest request
    );

    @Operation(summary = "itinerary item 삭제", description = "item을 삭제하고 day 내부 orderNo를 재압축합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "item 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<Void>> deleteItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID itemId
    );
}
