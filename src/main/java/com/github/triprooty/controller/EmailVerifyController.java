package com.github.triprooty.controller;

import com.github.triprooty.dto.request.auth.EmailCodeVerifyRequest;
import com.github.triprooty.dto.request.auth.EmailVerifyReqeust;
import com.github.triprooty.dto.response.auth.EmailCodeVerifyResponse;
import com.github.triprooty.dto.response.auth.EmailVerifyResponse;
import com.github.triprooty.global.dto.DataResponse;
import com.github.triprooty.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/email")
public class EmailVerifyController {

    private final AuthService authService;

    /** 인증번호 발송 */
    @PostMapping("/send")
    public ResponseEntity<DataResponse<EmailVerifyResponse>> send(@RequestBody @Valid EmailVerifyReqeust req) {
        return ResponseEntity.ok(DataResponse.from(authService.sendEmail(req)));
    }

    /** 인증번호 검증 */
    @PostMapping("/verify")
    public ResponseEntity<DataResponse<EmailCodeVerifyResponse>> verify(@RequestBody @Valid EmailCodeVerifyRequest req) {
        return ResponseEntity.ok(DataResponse.from(authService.verifyCode(req)));
    }
}
