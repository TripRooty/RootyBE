package com.github.triprooty.controller.docs;

import com.github.triprooty.dto.request.auth.*;
import com.github.triprooty.dto.response.auth.EmailCodeVerifyResponse;
import com.github.triprooty.dto.response.auth.TokenPairResponse;
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

@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthSwaggerSpec {

    @Operation(summary = "회원가입", description = "닉네임, 이메일과 비밀번호를 통해 회원가입합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "이메일 중복 (USER-004) / 닉네임 중복 (USER-007)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<Void>> signup(@RequestBody @Valid SignupRequest req);

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 통해 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "비밀번호 오류 (USER-005)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<TokenPairResponse>> signin(@RequestBody @Valid SigninRequest req);

    @Operation(summary = "리프레시", description = "리프레시 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리프레시 토큰 발급 성공"),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰 만료 (USER-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<TokenPairResponse>> refresh(@RequestBody @Valid RefreshRequest req);

    @Operation(summary = "로그아웃", description = "로그아웃한 기기의 정보를 모두 지웁니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
    })
    public ResponseEntity<DataResponse<Void>> signout(@AuthenticationPrincipal UserPrincipal userDetails, @RequestBody @Valid SignoutRequest req);

    @Operation(summary = "비밀번호 초기화", description = "이메일 인증 토큰으로 비밀번호를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "기존 비밀번호와 동일 (USER-015)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "이메일 인증 토큰 만료 (USER-017)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<Void>> resetPassword(@RequestBody @Valid ResetPasswordRequest req);

    @Operation(summary = "이메일 인증번호 발송", description = "사용자의 이메일로 인증번호를 전송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공"),
    })
    public ResponseEntity<DataResponse<Void>> send(@RequestBody @Valid EmailVerifyReqeust req);

    @Operation(summary = "이메일 인증번호 검증", description = "인증번호를 검증하고 이메일 인증 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증번호 검증 성공"),
            @ApiResponse(responseCode = "403", description = "이메일 인증 코드 만료 (USER-018) / 인증번호 미일치 (USER-019)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<EmailCodeVerifyResponse>> verify(@RequestBody @Valid EmailCodeVerifyRequest req);
}
