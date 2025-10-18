package com.github.triprooty.service;

import com.github.triprooty.domain.User;
import com.github.triprooty.dto.request.SigninRequest;
import com.github.triprooty.dto.request.SignupRequest;
import com.github.triprooty.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder PasswordEncoder;
    private final AuthenticationManager authManager;

    public void signup(SignupRequest req) {
        // TODO: user exception error code 교체
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

    public Authentication authenticate(SigninRequest req) {
        var token = new UsernamePasswordAuthenticationToken(req.email(), req.password());
        return authManager.authenticate(token);
    }
}
