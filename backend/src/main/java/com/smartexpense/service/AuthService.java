package com.smartexpense.service;

import com.smartexpense.dto.AuthDtos;
import com.smartexpense.entity.User;
import com.smartexpense.exception.ApiException;
import com.smartexpense.repository.UserRepository;
import com.smartexpense.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        user = userRepository.save(user);
        return buildResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return buildResponse(user);
    }

    private AuthDtos.AuthResponse buildResponse(User user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return AuthDtos.AuthResponse.builder()
                .token(token)
                .user(AuthDtos.UserSummary.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .build();
    }
}
