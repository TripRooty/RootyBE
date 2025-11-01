package com.github.triprooty.dto.response.auth;

public record TokenPairResponse(String accessToken, String refreshToken, String tokenType) {
    public TokenPairResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}
