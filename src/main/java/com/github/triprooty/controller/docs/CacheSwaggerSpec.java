package com.github.triprooty.controller.docs;

import com.github.triprooty.dto.request.travel.RouteResolveRequest;
import com.github.triprooty.dto.response.travel.PlaceSearchCacheResponse;
import com.github.triprooty.dto.response.travel.RouteCacheResponse;
import com.github.triprooty.global.dto.DataResponse;
import com.github.triprooty.global.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "PlaceRouteCache", description = "장소/경로 캐시 API")
public interface CacheSwaggerSpec {

    @Operation(summary = "장소 캐시 검색", description = "검색 캐시 hit 시 캐시 반환, miss 시 Google Places 호출 후 캐시 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<PlaceSearchCacheResponse>> cacheSearch(@RequestParam String query);

    @Operation(summary = "경로 캐시 조회", description = "fromLocationId + toLocationId + transportType으로 경로 캐시를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<RouteCacheResponse>> getRouteCache(
            @RequestParam UUID fromLocationId,
            @RequestParam UUID toLocationId,
            @RequestParam String transportType
    );

    @Operation(summary = "경로 계산/캐시 저장", description = "경로 캐시 miss 시 외부 API를 호출해 캐시 저장 후 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해결 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "location 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<RouteCacheResponse>> resolveRoute(
            @Valid @RequestBody RouteResolveRequest request
    );
}
