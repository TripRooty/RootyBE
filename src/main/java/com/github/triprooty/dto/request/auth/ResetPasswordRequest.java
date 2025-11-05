package com.github.triprooty.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest (
        @NotBlank String emailVerifiedToken,
        @NotBlank @Email String email,
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-]).{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8~20자여야 합니다."
        )
        String password
) {}
