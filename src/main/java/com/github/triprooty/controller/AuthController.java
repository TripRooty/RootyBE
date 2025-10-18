package com.github.triprooty.controller;

import com.github.triprooty.dto.request.SigninRequest;
import com.github.triprooty.dto.request.SignupRequest;
import com.github.triprooty.dto.response.TokenResponse;
import com.github.triprooty.global.security.JwtTokenProvider;
import com.github.triprooty.global.security.UserPrincipal;
import com.github.triprooty.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwt;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest req) {
        authService.signup(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> signin(@RequestBody @Valid SigninRequest req) {
        var auth = authService.authenticate(req);
        var principal = (UserPrincipal) auth.getPrincipal();
        String token = jwt.createToken(principal.getEmail(), principal.getId());
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
