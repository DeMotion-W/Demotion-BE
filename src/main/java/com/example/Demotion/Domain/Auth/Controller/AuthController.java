package com.example.Demotion.Domain.Auth.Controller;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
import com.example.Demotion.Domain.Auth.Config.JwtUtil;
import com.example.Demotion.Domain.Auth.Dto.*;
import com.example.Demotion.Domain.Auth.Repository.RefreshTokenRepository;
import com.example.Demotion.Domain.Auth.Service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody SignupRequestDto request) {
        authService.registerUser(
                request.getEmail(),
                request.getName(),
                request.getPassword()
        );
        return ResponseEntity.status(201).build(); // body 없이 상태만
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        try {
            LoginResponseDto result = authService.login(request.email(), request.password(), response);
            return ResponseEntity.ok(result);

        } catch (UsernameNotFoundException | BadCredentialsException e) {
            throw new ErrorDomain(ErrorCode.INVALID_CREDENTIALS);
        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 토큰 갱신
    @PostMapping("/login-refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        try {
            AccessTokenResponseDto newToken = authService.refreshAccessToken(refreshToken, response);
            return ResponseEntity.ok(newToken);


        } catch (ErrorDomain e) {
            throw e; // GlobalExceptionHandler에서 처리
        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    // 로그아웃
    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                    HttpServletResponse response) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ErrorDomain(ErrorCode.MISSING_AUTHORIZATION_HEADER);
        }

        String token = authHeader.replace("Bearer ", "");
        authService.logout(token, response);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String code, @RequestParam String newPassword) {
        try {
            authService.resetPassword(email, code, newPassword);
            return ResponseEntity.ok("비밀번호 재설정 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}