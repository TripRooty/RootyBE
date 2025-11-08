package com.github.triprooty.controller;

import com.github.triprooty.controller.docs.AuthSwaggerSpec;
import com.github.triprooty.dto.request.auth.*;
import com.github.triprooty.dto.response.auth.EmailCodeVerifyResponse;
import com.github.triprooty.dto.response.auth.TokenPairResponse;
import com.github.triprooty.global.dto.DataResponse;
import com.github.triprooty.global.security.UserPrincipal;
import com.github.triprooty.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthSwaggerSpec {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<DataResponse<Void>> signup(@RequestBody @Valid SignupRequest req) {
        authService.signup(req);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @PostMapping("/signin")
    public ResponseEntity<DataResponse<TokenPairResponse>> signin(@RequestBody @Valid SigninRequest req) {
        return ResponseEntity.ok(DataResponse.from(authService.signin(req)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<DataResponse<TokenPairResponse>> refresh(@RequestBody @Valid RefreshRequest req) {
        return ResponseEntity.ok(DataResponse.from(authService.refresh(req)));
    }

    @PostMapping("/signout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DataResponse<Void>> signout(
            @AuthenticationPrincipal UserPrincipal userDetails,
            @RequestBody @Valid SignoutRequest req
    ) {
        authService.signout(userDetails.getEmail(), req);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<DataResponse<Void>> resetPassword(@RequestBody @Valid ResetPasswordRequest req) {
        authService.resetPassword(req);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @PostMapping("/email/send")
    public ResponseEntity<DataResponse<Void>> send(@RequestBody @Valid EmailVerifyReqeust req) {
        authService.sendEmail(req);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @PostMapping("/email/verify")
    public ResponseEntity<DataResponse<EmailCodeVerifyResponse>> verify(@RequestBody @Valid EmailCodeVerifyRequest req) {
        return ResponseEntity.ok(DataResponse.from(authService.verifyCode(req)));
    }
}
