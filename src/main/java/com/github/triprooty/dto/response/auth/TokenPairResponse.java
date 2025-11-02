package com.github.triprooty.dto.response.auth;

public record TokenPairResponse(String accessToken, String refreshToken, String deviceId, String tokenType) {
    public TokenPairResponse(String accessToken, String refreshToken, String deviceId) {
        this(accessToken, refreshToken, deviceId, "Bearer");
    }
}
