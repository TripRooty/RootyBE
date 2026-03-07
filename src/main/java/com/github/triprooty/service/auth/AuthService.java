package com.github.triprooty.service.auth;

import com.github.triprooty.domain.User;
import com.github.triprooty.dto.request.auth.*;
import com.github.triprooty.dto.response.auth.EmailCodeVerifyResponse;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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

    @Value("${jwt.refresh-token-expire-ms}")
    long refreshTokenExpireMs;

    // =========================
    // Redis Key Helpers
    // =========================
    private String rtKey(String rtHash) { return "rt:" + rtHash; }

    // 인덱스: email -> rtHash set
    private String idxEmail(String email) { return "rti:email:" + normalizeEmail(email); }

    // 인덱스: deviceId -> rtHash set (선택이지만 운영/추적에 유용)
    private String idxDev(String deviceId) { return "rti:dev:" + deviceId; }

    // 인덱스: email + deviceId -> rtHash set  (기기별 로그아웃 핵심)
    private String idxEmailDev(String email, String deviceId) {
        return "rti:emaildev:" + normalizeEmail(email) + ":" + deviceId;
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.toLowerCase(Locale.ROOT).trim();
    }

    // =========================
    // RT Payload (Redis Value)
    // =========================
    /**
     * Redis rt:{hash} value에 최소한으로 userId + deviceId를 저장.
     * (실무에서는 JSON으로도 많이 저장하지만, 의존성/오버헤드 줄이려면 delim도 충분)
     */
    private record RtPayload(UUID userId, String deviceId, long issuedAtEpochSec) {

        String encode() {
            // uid:did:iat
            return userId + ":" + deviceId + ":" + issuedAtEpochSec;
        }

        static RtPayload decode(String raw) {
            if (!StringUtils.hasText(raw)) return null;
            String[] parts = raw.split(":", 3);
            if (parts.length != 3) return null;
            try {
                UUID uid = UUID.fromString(parts[0]);
                String did = parts[1];
                long iat = Long.parseLong(parts[2]);
                return new RtPayload(uid, did, iat);
            } catch (RuntimeException e) {
                return null;
            }
        }
    }

    // =========================
    // Email verify html
    // =========================
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
        return "auth:code:" + normalizeEmail(email);
    }

    public String emailVerifiedKey(String email) {
        return "evk:" + normalizeEmail(email);
    }

    private String gen6() {
        int n = RANDOM.nextInt(1_000_000); // 0..999999
        return String.format("%06d", n);
    }

    // =========================
    // DeviceId policy
    // =========================
    /**
     * 실무 권장: deviceId는 클라가 1회 생성해 영구 저장 후 항상 내려주기.
     * - 다만 기존 클라/웹 호환을 위해 비어있으면 서버가 발급해서 응답에 포함.
     * - 완전 엄격하게 갈 거면 여기서 예외 던지면 됨.
     */
    private String resolveDeviceId(String maybeDeviceId) {
        if (StringUtils.hasText(maybeDeviceId)) return maybeDeviceId.trim();
        return UUID.randomUUID().toString();
    }

    // =========================
    // Token issue / revoke
    // =========================
    private TokenPairResponse issueTokens(String email, UUID userId, String deviceId) {
        String access = jwtTokenProvider.createToken(email, userId);

        String rtPlain = TokenUtils.newToken();
        String rtHash = TokenUtils.sha256Hex(rtPlain);

        RtPayload payload = new RtPayload(userId, deviceId, Instant.now().getEpochSecond());

        // 본문 저장 + 인덱스 등록
        // (원자성 100% 필요하면 Lua로 묶을 수 있지만, 보통은 이 정도로 충분)
        redis.opsForValue().set(rtKey(rtHash), payload.encode(), refreshTokenExpireMs, TimeUnit.MILLISECONDS);

        redis.opsForSet().add(idxEmail(email), rtHash);
        redis.opsForSet().add(idxDev(deviceId), rtHash);
        redis.opsForSet().add(idxEmailDev(email, deviceId), rtHash);

        // 인덱스 TTL도 맞춰서 고아 인덱스 줄이기
        expireIndexKeys(email, deviceId);

        return new TokenPairResponse(access, rtPlain, deviceId);
    }

    private void expireIndexKeys(String email, String deviceId) {
        redis.expire(idxEmail(email), refreshTokenExpireMs, TimeUnit.MILLISECONDS);
        redis.expire(idxDev(deviceId), refreshTokenExpireMs, TimeUnit.MILLISECONDS);
        redis.expire(idxEmailDev(email, deviceId), refreshTokenExpireMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 단일 RT 제거: 본문 + 인덱스 모두 정리
     */
    private void revokeSingle(String rtHash, String email, String deviceId) {
        redis.delete(rtKey(rtHash));
        redis.opsForSet().remove(idxEmail(email), rtHash);
        redis.opsForSet().remove(idxDev(deviceId), rtHash);
        redis.opsForSet().remove(idxEmailDev(email, deviceId), rtHash);
    }

    // =========================
    // Auth flows
    // =========================
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

    @Transactional
    public TokenPairResponse signin(@Valid SigninRequest req) {
        if (!userRepository.existsByEmail(req.email())) {
            throw new AppException(UserErrorCode.USER_NOT_FOUND);
        }

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password())
            );
            UserPrincipal p = (UserPrincipal) auth.getPrincipal();

            String deviceId = resolveDeviceId(req.deviceId());

            // email은 principal 기준(정규화/케이스 이슈 방지)
            return issueTokens(p.getEmail(), p.getId(), deviceId);

        } catch (AuthenticationException e) {
            throw new AppException(UserErrorCode.INVALID_PASSWORD);
        }
    }

    /**
     * refresh:
     * - rt:{hash} 에 저장된 deviceId와 요청 deviceId가 반드시 일치해야 함
     * - 일치하면 기존 RT 회수 후 새 RT 발급(회전)
     */
    @Transactional
    public TokenPairResponse refresh(@Valid RefreshRequest req) {
        String deviceId = resolveDeviceId(req.deviceId()); // 엄격 모드면 resolve 대신 "required" 체크로 바꾸기

        String oldPlain = req.refreshToken();
        String oldHash = TokenUtils.sha256Hex(oldPlain);

        String raw = redis.opsForValue().get(rtKey(oldHash));
        RtPayload payload = RtPayload.decode(raw);
        if (payload == null) throw new AppException(UserErrorCode.INVALID_REFRESH_TOKEN);

        if (!deviceId.equals(payload.deviceId())) {
            // 다른 기기에서 RT 탈취/재사용 방지
            throw new AppException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        UUID userId = payload.userId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        // 기존 RT 제거
        revokeSingle(oldHash, user.getEmail(), deviceId);

        // 새 RT 발급
        return issueTokens(user.getEmail(), user.getId(), deviceId);
    }

    /**
     * 기기별 로그아웃:
     * - email + deviceId 조합으로 연결된 RT 해시 전부 회수
     */
    @Transactional
    public void signout(String email, @Valid SignoutRequest req) {
        String deviceId = resolveDeviceId(req.deviceId()); // 엄격 모드면 required 체크

        String normalizedEmail = normalizeEmail(email);
        var key = idxEmailDev(normalizedEmail, deviceId);

        Set<String> hashes = redis.opsForSet().members(key);
        if (hashes == null || hashes.isEmpty()) return;

        for (String h : hashes) {
            redis.delete(rtKey(h));
            redis.opsForSet().remove(idxEmail(normalizedEmail), h);
            redis.opsForSet().remove(idxDev(deviceId), h);
        }

        // 조합 인덱스는 통째로 제거
        redis.delete(key);
    }

    /**
     * 전체 로그아웃(이메일 기준):
     * - email 인덱스에 있는 모든 RT를 회수
     * - rt payload에 들어있는 deviceId로 dev/emaildev 인덱스까지 정밀 정리
     */
    @Transactional
    public void signoutAllByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        String emailIdxKey = idxEmail(normalizedEmail);

        Set<String> hashes = redis.opsForSet().members(emailIdxKey);
        if (hashes == null || hashes.isEmpty()) return;

        // rt payload를 읽어 deviceId까지 정리
        List<String> rtKeys = hashes.stream().map(this::rtKey).toList();
        List<String> raws = redis.opsForValue().multiGet(rtKeys);

        // 1) 본문 키 삭제
        redis.delete(rtKeys);

        // 2) 인덱스 정리
        int i = 0;
        for (String h : hashes) {
            String raw = (raws != null && raws.size() > i) ? raws.get(i) : null;
            i++;

            RtPayload payload = RtPayload.decode(raw);
            if (payload == null) continue;

            String deviceId = payload.deviceId();

            redis.opsForSet().remove(idxDev(deviceId), h);
            redis.opsForSet().remove(idxEmailDev(normalizedEmail, deviceId), h);
        }

        // 3) email 인덱스 제거
        redis.delete(emailIdxKey);

        // 4) 남아있는 emaildev:* 빈 set은 TTL로 자연 소멸(또는 운영 배치로 스캔 정리)
    }

    // =========================
    // Password reset
    // =========================
    @Transactional
    public void resetPassword(@Valid ResetPasswordRequest req) {
        final String email = normalizeEmail(req.email());
        final String password = req.password();

        String savedToken = redis.opsForValue().get(emailVerifiedKey(email));
        if (!StringUtils.hasText(savedToken) || !req.emailVerifiedToken().equals(savedToken)) {
            throw new AppException(UserErrorCode.EMAIL_VERIFY_TOKEN_EXPIRED);
        }

        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if (passwordEncoder.matches(password, user.getPassword())) {
            throw new AppException(UserErrorCode.SAME_AS_OLD_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(password));
        redis.delete(emailVerifiedKey(email));
    }

    // =========================
    // Email verify
    // =========================
    @Transactional
    public void sendEmail(EmailVerifyReqeust req) {
        if (!userRepository.existsByEmail(req.email())) {
            throw new AppException(UserErrorCode.USER_NOT_FOUND);
        }

        String email = normalizeEmail(req.email());
        String code = gen6();

        try {
            redis.delete(codeKey(email));
            redis.opsForValue().set(codeKey(email), code, CODE_TTL);
        } catch (DataAccessException e) {
            throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[TripRooty] 이메일 인증");

            helper.setText(buildVerifyHtml(code), true);
            mailSender.send(mime);
        } catch (MessagingException e) {
            throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public EmailCodeVerifyResponse verifyCode(EmailCodeVerifyRequest req) {
        String email = normalizeEmail(req.email());
        String key = codeKey(email);

        String saved = redis.opsForValue().get(key);
        if (!StringUtils.hasText(saved)) throw new AppException(UserErrorCode.EMAIL_VERIFY_CODE_EXPIRED);

        if (!saved.equals(req.code())) {
            throw new AppException(UserErrorCode.EMAIL_VERIFY_CODE_MISMATCH);
        }

        redis.delete(key);

        String emailVerifiedToken = TokenUtils.newToken();
        redis.opsForValue().set(emailVerifiedKey(email), emailVerifiedToken, CODE_TTL);

        return new EmailCodeVerifyResponse(emailVerifiedToken);
    }
}
