package com.github.triprooty.dto.response;

public record TokenPairResponse(String accessToken, String refreshToken, String tokenType) {
    public TokenPairResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}