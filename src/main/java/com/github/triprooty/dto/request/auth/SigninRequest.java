package com.github.triprooty.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SigninRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        String deviceId,         // optional: 로컬 스토리지 저장: 없으면 서버가 생성해 반환
        String deviceName        // optional: 로컬 정보 가져오기
) { }
