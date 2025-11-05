package com.github.triprooty.dto.request.auth;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @Email @NotBlank String email,
        @NotBlank String refreshToken,
        @NotBlank @Nonnull String deviceId
) { }
