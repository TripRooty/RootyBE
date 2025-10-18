package com.github.triprooty.dto.response;

public record TokenResponse(String accessToken, String tokenType) {
    public TokenResponse(String accessToken) { this(accessToken, "Bearer"); }
}