package com.github.triprooty.service.user;

import com.github.triprooty.domain.User;
import com.github.triprooty.dto.request.auth.RefreshRequest;
import com.github.triprooty.dto.request.auth.SigninRequest;
import com.github.triprooty.dto.request.auth.SignoutRequest;
import com.github.triprooty.dto.request.auth.SignupRequest;
import com.github.triprooty.dto.response.auth.TokenPairResponse;
import com.github.triprooty.global.security.JwtTokenProvider;
import com.github.triprooty.global.security.TokenUtils;
import com.github.triprooty.global.security.UserPrincipal;
import com.github.triprooty.repository.UserRepository;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder PasswordEncoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    private String rtKey(String hash) { return "rt:" + hash; }

    @Value("${jwt.refresh-token-expire-ms}") long refreshTokenExpireMs;

    @Transactional
    public void signup(SignupRequest req) {
        // TODO: 에러코드 사용하여 수정
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByName(req.name())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        userRepository.save(User.builder()
                .email(req.email())
                .name(req.name())
                .password(PasswordEncoder.encode(req.password()))
                .provider("local")
                .build());
    }

    @Transactional
    public TokenPairResponse signin(@RequestBody @Valid SigninRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );
        UserPrincipal p = (UserPrincipal) auth.getPrincipal();

        // 1) DB 재조회 제거
        String access = jwtTokenProvider.createToken(p.getEmail(), p.getId());

        // 2) RT 발급 + TTL 저장
        String rtPlain = TokenUtils.newRefreshToken();
        String rtHash  = TokenUtils.sha256Hex(rtPlain);
        redisTemplate.opsForValue()
                .set(rtKey(rtHash), p.getId().toString(), refreshTokenExpireMs, TimeUnit.MILLISECONDS);

        return new TokenPairResponse(access, rtPlain);
    }


    @Transactional
    public TokenPairResponse refresh(@RequestBody @Valid RefreshRequest req) {
        String provided = req.refreshToken();
        String hash = TokenUtils.sha256Hex(provided);

        String key = rtKey(hash);
        String userIdStr = redisTemplate.opsForValue().get(key);
        if (userIdStr == null) {
            // TODO: 에러코드 사용하여 수정
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId).orElseThrow();

        redisTemplate.delete(key);

        String newRtPlain = TokenUtils.newRefreshToken();
        String newRtHash  = TokenUtils.sha256Hex(newRtPlain);
        redisTemplate.opsForValue().set(rtKey(newRtHash), userId.toString());

        String newAccess = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        return new TokenPairResponse(newAccess, newRtPlain);
    }

    @Transactional
    public void signout(@RequestBody @Valid SignoutRequest req) {
        String hash = TokenUtils.sha256Hex(req.refreshToken());
        redisTemplate.delete(rtKey(hash));
    }
}
