package com.github.triprooty.dto.response.auth;

import java.time.Duration;

public record EmailCodeVerifyResponse(Boolean success, String emailVerifiedToken) {
    public EmailCodeVerifyResponse(Boolean success) {
        this(success, "");
    }
    public EmailCodeVerifyResponse(String emailVerifiedToken) {
        this(true, emailVerifiedToken);
    }
}
