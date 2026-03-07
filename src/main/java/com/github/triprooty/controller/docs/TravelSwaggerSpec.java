package com.github.triprooty.controller.docs;

import com.github.triprooty.dto.request.travel.TravelCreateRequest;
import com.github.triprooty.dto.request.travel.TravelDayCreateRequest;
import com.github.triprooty.dto.request.travel.TravelDayReorderRequest;
import com.github.triprooty.dto.request.travel.TravelUpdateRequest;
import com.github.triprooty.dto.response.travel.TravelDayResponse;
import com.github.triprooty.dto.response.travel.TravelDetailResponse;
import com.github.triprooty.dto.response.travel.TravelSummaryResponse;
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
@Tag(name = "Travel", description = "여행 관련 API")
public interface TravelSwaggerSpec {

    @Operation(summary = "여행 목록 조회", description = "사용자가 접근 가능한 여행 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<List<TravelSummaryResponse>>> getTravels();

    @Operation(summary = "여행 생성", description = "여행을 생성하고 기간 기준으로 TravelDay를 자동 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<TravelSummaryResponse>> createTravel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody TravelCreateRequest request
    );

    @Operation(summary = "여행 상세 조회", description = "여행 상세와 day/item 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "여행 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<TravelDetailResponse>> getTravelDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId
    );

    @Operation(summary = "여행 수정", description = "여행 메타 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 수정 성공"),
            @ApiResponse(responseCode = "400", description = "권한 없음 또는 잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "여행 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<Void>> updateTravel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId,
            @RequestBody TravelUpdateRequest request
    );

    @Operation(summary = "여행 삭제", description = "여행을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "여행 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<Void>> deleteTravel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId
    );

    @Operation(summary = "여행 day 목록 조회", description = "여행의 day 목록을 orderNo 순서로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "day 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "여행 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<List<TravelDayResponse>>> getTravelDays(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId
    );

    @Operation(summary = "여행 day 추가", description = "여행에 day를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "day 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "여행 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<TravelDayResponse>> createTravelDay(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId,
            @Valid @RequestBody TravelDayCreateRequest request
    );

    @Operation(summary = "여행 day 순서 변경", description = "여행 day의 orderNo를 일괄 재정렬합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "day 순서 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "여행 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<List<TravelDayResponse>>> reorderTravelDays(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId,
            @Valid @RequestBody TravelDayReorderRequest request
    );

    @Operation(summary = "여행 스크랩", description = "여행을 스크랩합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스크랩 성공"),
            @ApiResponse(responseCode = "404", description = "여행 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<Void>> scrapTravel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId
    );

    @Operation(summary = "여행 스크랩 취소", description = "여행 스크랩을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스크랩 취소 성공"),
            @ApiResponse(responseCode = "404", description = "여행 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<Void>> unscrapTravel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID travelId
    );
}
