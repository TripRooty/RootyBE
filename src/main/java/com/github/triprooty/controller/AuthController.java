package com.github.triprooty.controller;

import com.github.triprooty.dto.request.auth.RefreshRequest;
import com.github.triprooty.dto.request.auth.SigninRequest;
import com.github.triprooty.dto.request.auth.SignoutRequest;
import com.github.triprooty.dto.request.auth.SignupRequest;
import com.github.triprooty.dto.response.auth.TokenPairResponse;
import com.github.triprooty.global.dto.DataResponse;
import com.github.triprooty.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<DataResponse<Void>> signup(@RequestBody @Valid SignupRequest req) {
        authService.signup(req);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<DataResponse<Void>> signout(@RequestBody @Valid SignoutRequest req) {
        authService.signout(req);
        return ResponseEntity.ok().build();
    }
}
