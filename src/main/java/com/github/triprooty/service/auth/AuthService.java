package com.github.triprooty.service.auth;

import com.github.triprooty.domain.User;
import com.github.triprooty.dto.request.auth.*;
import com.github.triprooty.dto.response.auth.EmailCodeVerifyResponse;
import com.github.triprooty.dto.response.auth.EmailVerifyResponse;
import com.github.triprooty.dto.response.auth.TokenPairResponse;
import com.github.triprooty.global.exception.AppException;
import com.github.triprooty.global.exception.common.CommonErrorCode;
import com.github.triprooty.global.exception.user.UserErrorCode;
import com.github.triprooty.global.exception.user.UserNotFoundException;
import com.github.triprooty.global.security.JwtTokenProvider;
import com.github.triprooty.global.security.TokenUtils;
import com.github.triprooty.global.security.UserPrincipal;
import com.github.triprooty.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redis;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Duration CODE_TTL = Duration.ofMinutes(5);

    @Value("${jwt.refresh-token-expire-ms}") long refreshTokenExpireMs;

    // ===== Redis Key Helpers =====
    private String rtKey(String hash) { return "rt:" + hash; }
    private String idxEmail(String email) { return "rti:email:" + email.toLowerCase(); }
    private String idxDev(String deviceId) { return "rti:dev:" + deviceId; }
    private String idxEmailDev(String email, String deviceId) {
        return "rti:emaildev:" + email.toLowerCase() + ":" + deviceId;
    }

    private String buildVerifyHtml(String code) {
        return """
    <!doctype html>
    <html lang="ko">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/fonts-archive/Paperlogy/Paperlogy.css" type="text/css"/>
    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1" />
      <title>TripRooty 인증번호</title>
    </head>
    <body style="margin:0;background:#f7f9fc;">
      <div style="
        max-width:520px;margin:0 auto;padding:24px;background:#f7f9fc;
        font-family:'Paperlogy',-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,
                     'Apple SD Gothic Neo','Pretendard',system-ui,sans-serif;
        color:#1f2937;
      ">
        <div style="
          font-weight:800;font-size:26px;letter-spacing:.2px;color:#5B85EE;
          margin-bottom:24px;
          font-family:'Paperlogy',-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,
                       'Apple SD Gothic Neo','Pretendard',system-ui,sans-serif;
        ">TripRooty</div>

        <div style="background:#ffffff;border-radius:12px;padding:28px;border:1px solid #e5e7eb;">
          <div style="margin:16px 0 8px;font-size:18px;font-weight:700;">이메일 인증번호</div>

          <p style="margin:0;line-height:1.6;color:#4b5563;font-size:14px;">
            아래 인증번호를 입력해 주세요.
          </p>
          <p style="margin:4px 0 0;line-height:1.6;color:#4b5563;font-size:14px;">
            보안 유지를 위해 5분 이내에 사용해야 합니다.
          </p>

          <div style="text-align:center;margin-top:14px;">
            <span style="
              display:inline-block;font-size:28px;font-weight:800;letter-spacing:6px;
              padding:12px 16px;border-radius:10px;background:#5B85EE;color:#ffffff;
              font-family:'Paperlogy',-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,
                           'Apple SD Gothic Neo','Pretendard',system-ui,sans-serif;
            ">%s</span>
          </div>

          <div style="margin-top:16px;color:#6b7280;font-size:12px;text-align:left;">
            요청하지 않은 메일이라면 이 메시지는 무시하셔도 됩니다.
          </div>
        </div>

        <div style="margin-top:20px;color:#9ca3af;font-size:12px;text-align:left;">
          TripRooty. 이 메일은 발신 전용입니다.
        </div>
      </div>
    </body>
    </html>
    """.formatted(code);
    }

    private String codeKey(String email) {
        return "auth:code:" + email.toLowerCase();
    }

    public String emailVerifiedKey(String email) {
        return "evk:" + email.toLowerCase();
    }

    private String gen6() {
        int n = RANDOM.nextInt(1_000_000); // 0..999999
        return String.format("%06d", n);
    }

    // ===== 토큰 발급 공통 로직 =====
    private TokenPairResponse issueTokens(String email, UUID userId, String deviceId) {
        String access = jwtTokenProvider.createToken(email, userId);

        String rtPlain = TokenUtils.newToken();
        String rtHash  = TokenUtils.sha256Hex(rtPlain);

        // 본문 저장 (TTL)
        redis.opsForValue().set(rtKey(rtHash), userId.toString(),
                refreshTokenExpireMs, TimeUnit.MILLISECONDS);

        // 인덱스 등록
        redis.opsForSet().add(idxEmail(email), rtHash);
        redis.opsForSet().add(idxDev(deviceId), rtHash);
        redis.opsForSet().add(idxEmailDev(email, deviceId), rtHash);

        // 인덱스 키에도 TTL을 걸어두면 고아 인덱스 정리에 도움됨(선택)
        redis.expire(idxEmail(email), refreshTokenExpireMs, TimeUnit.MILLISECONDS);
        redis.expire(idxDev(deviceId), refreshTokenExpireMs, TimeUnit.MILLISECONDS);
        redis.expire(idxEmailDev(email, deviceId), refreshTokenExpireMs, TimeUnit.MILLISECONDS);

        return new TokenPairResponse(access, rtPlain, deviceId);
    }

    @Transactional
    public void signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.email())) throw new AppException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        if (userRepository.existsByName(req.name())) throw new AppException(UserErrorCode.DUPLICATE_NICKNAME);
        userRepository.save(User.builder()
                .email(req.email())
                .name(req.name())
                .password(passwordEncoder.encode(req.password()))
                .provider("local")
                .build());
    }

    // 로그인: email + password + deviceId 필요
    @Transactional
    public TokenPairResponse signin(@Valid SigninRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );
        UserPrincipal p = (UserPrincipal) auth.getPrincipal();

        String deviceId = (req.deviceId() == null || req.deviceId().isBlank())
                ? UUID.randomUUID().toString()
                : req.deviceId();

        return issueTokens(p.getEmail(), p.getId(), deviceId);
    }

    // 리프레시: 기존 RT 단일 회전 + 인덱스 갱신
    @Transactional
    public TokenPairResponse refresh(@Valid RefreshRequest req) {
        String oldPlain = req.refreshToken();
        String oldHash  = TokenUtils.sha256Hex(oldPlain);

        String userIdStr = redis.opsForValue().get(rtKey(oldHash));
        if (userIdStr == null) throw new AppException(UserErrorCode.INVALID_REFRESH_TOKEN);

        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId).orElseThrow();

        // 기존 RT 제거 + 인덱스에서 제거
        revokeSingle(oldHash, user.getEmail(), req.deviceId());

        // 새 RT 발급
        return issueTokens(user.getEmail(), user.getId(), req.deviceId());
    }

    // 단일 RT 제거(리프레시 회전/수동 로그아웃에서 사용)
    private void revokeSingle(String rtHash, String email, String deviceId) {
        redis.delete(rtKey(rtHash));
        redis.opsForSet().remove(idxEmail(email), rtHash);
        redis.opsForSet().remove(idxDev(deviceId), rtHash);
        redis.opsForSet().remove(idxEmailDev(email, deviceId), rtHash);
    }

    @Transactional
    public void signout(@Valid SignoutRequest req) {
        final String email = req.email();
        final String deviceId = req.deviceId();

        // 1) 해당 email+deviceId 조합의 모든 해시 가져오기
        var hashes = redis.opsForSet().members(idxEmailDev(email, deviceId));
        if (hashes == null || hashes.isEmpty()) return;

        // 2) 각 해시에 대해 본문/인덱스 모두 제거
        for (String h : hashes) {
            redis.delete(rtKey(h));
            redis.opsForSet().remove(idxEmail(email), h);
            redis.opsForSet().remove(idxDev(deviceId), h);
        }
        // 3) 조합 인덱스 세트 자체도 비우고/삭제
        redis.delete(idxEmailDev(email, deviceId));
    }

    @Transactional
    public void resetPassword(@Valid ResetPasswordRequest req) {
        final String email = req.email();
        final String password = req.password();

        String savedToken = redis.opsForValue().get(emailVerifiedKey(email));
        if (!req.emailVerifiedToken().equals(savedToken)) {
            throw new AppException(UserErrorCode.PASSWORD_RESET_TOKEN_EXPIRED);
        }

        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if(passwordEncoder.matches(password, user.getPassword())){
            throw new AppException(UserErrorCode.SAME_AS_OLD_PASSWORD); //USER-015
        }
        user.updatePassword(passwordEncoder.encode(password));
    }

    // (선택) 전체 로그아웃: 이메일의 모든 기기 토큰 제거
    @Transactional
    public void signoutAllByEmail(String email) {
        var hashes = redis.opsForSet().members(idxEmail(email));
        if (hashes == null || hashes.isEmpty()) return;

        for (String h : hashes) {
            redis.delete(rtKey(h));
            // dev 인덱스는 어떤 dev인지 모름 -> 정밀 제거는 선택 사항
            // 안전하게는 스캔 기반으로 emaildev:* 제거, 여기선 간단화
        }
        redis.delete(idxEmail(email));
        // emaildev:* 정리는 운영 배치 또는 SCAN으로 처리 가능
    }

    @Transactional
    public EmailVerifyResponse sendEmail(EmailVerifyReqeust req) {
        // 1) 회원 존재 여부 확인
        if (!userRepository.existsByEmail(req.email())) {
            throw new AppException(UserErrorCode.USER_NOT_FOUND);
        }
        // 2) 코드 생성 + 저장(5분 TTL, 덮어쓰기)
        String code = gen6();
        try {
            redis.delete(codeKey(req.email()));
            redis.opsForValue().set(codeKey(req.email()), code, CODE_TTL);
        } catch (DataAccessException e) {
            throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setTo(req.email());
            helper.setSubject("[TripRooty] 이메일 인증");

            String html = buildVerifyHtml(code);
            helper.setText(html, true);

            mailSender.send(mime);
        } catch (MessagingException e) {
            return new EmailVerifyResponse(false);
        }
        return new EmailVerifyResponse(true);
    }

    @Transactional
    public EmailCodeVerifyResponse verifyCode(EmailCodeVerifyRequest req) {
        String key = codeKey(req.email());

        // Redis 6.2+면 getAndDelete 사용 (Spring Data 3.x 지원)
        String saved = redis.opsForValue().get(key);
        if (saved == null) throw new AppException(UserErrorCode.EMAIL_VERIFY_CODE_EXPIRED);

        if (!saved.equals(req.code())) {
            throw new AppException(UserErrorCode.EMAIL_VERIFY_CODE_MISMATCH);
        }

        // 성공 시 즉시 삭제 (원자성을 원하면 Lua 스크립트 사용 가능)
        redis.delete(key);

        String emailVerifiedToken = TokenUtils.newToken();
        redis.opsForValue().set(emailVerifiedKey(req.email()), emailVerifiedToken, CODE_TTL);

        return new EmailCodeVerifyResponse(emailVerifiedToken);
    }
}
