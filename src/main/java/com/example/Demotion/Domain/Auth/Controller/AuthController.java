package com.example.Demotion.Domain.Auth.Controller;

import com.example.Demotion.Domain.Auth.Config.JwtUtil;
import com.example.Demotion.Domain.Auth.Dto.*;
import com.example.Demotion.Domain.Auth.Entity.RefreshToken;
import com.example.Demotion.Domain.Auth.Repository.RefreshTokenRepository;
import com.example.Demotion.Domain.Auth.Service.CustomUserDetailService;
import com.example.Demotion.Domain.Auth.Service.EmailService;
import com.example.Demotion.Domain.Auth.Service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;

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
        try {
            authService.registerUser(request.getEmail(), request.getName(), request.getPassword());
            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        try {
            LoginResponseDto result = authService.login(request.getEmail(), request.getPassword(), response);
            return ResponseEntity.ok().body(result);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("비밀번호가 일치하지 않습니다.");
        }
    }

    // 토큰 갱신
    @PostMapping("/login-refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        try {
            LoginResponseDto newToken = authService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(newToken);
        } catch (JwtException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader, HttpServletResponse response) {
        String token = authHeader.replace("Bearer ", "");
        authService.logout(token, response);
        return ResponseEntity.ok("로그아웃되었습니다.");
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