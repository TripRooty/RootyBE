package com.github.triprooty.global.security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class TokenUtils {
    private static final SecureRandom RNG = new SecureRandom();
    private TokenUtils(){}

    public static String newRefreshToken() {
        byte[] bytes = new byte[48]; // 384-bit
        RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(value.getBytes());
            StringBuilder sb = new StringBuilder(d.length * 2);
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
