package com.github.triprooty.controller.docs;

import com.github.triprooty.dto.request.user.PasswordChangeRequest;
import com.github.triprooty.dto.request.user.UserInfoRequest;
import com.github.triprooty.dto.response.user.UserInfoResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "유저 관련 API")
public interface UserSwaggerSpec {

    @Operation(summary = "내 정보 조회", description = "로그인된 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<UserInfoResponse>> getMyInfo(
            @AuthenticationPrincipal UserPrincipal userDetails);

    @Operation(summary = "내 정보 수정", description = "로그인된 사용자의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<Void>> updateUserInfo(
            @AuthenticationPrincipal UserPrincipal userDetails,
            @Valid @RequestBody UserInfoRequest request);

    @Operation(summary = "비밀번호 재설정", description = "현재 비밀번호로 인증하여, 비밀번호를 재설정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "일치하지 않는 비밀번호 (USER-006)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "새 비밀번호가 기존 비밀번호와 동일 (USER-015)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<Void>> updatePassword(
            @AuthenticationPrincipal UserPrincipal userDetails,
            @Valid @RequestBody PasswordChangeRequest request);

    @Operation(summary = "프로필 사진 업로드", description = "사용자의 프로필 이미지를 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 업로드 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 파일 형식 (FILE-005)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<String>> uploadProfileImage(
            @AuthenticationPrincipal UserPrincipal userDetails
            , @RequestParam("file") MultipartFile file) throws IOException;


    // TODO : 아래 문서 수정
    @Operation(summary = "회원 탈퇴", description = "회원탈퇴를 진행합니다.(soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<Void>> deleteUser(
            @AuthenticationPrincipal UserPrincipal userDetails);

}
