package com.github.triprooty.dto.request.auth;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignoutRequest(
        @Email String email,
        @NotBlank @Nonnull String deviceId
) { }

