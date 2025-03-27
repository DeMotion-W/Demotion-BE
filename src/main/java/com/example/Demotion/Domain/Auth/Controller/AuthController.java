package com.example.Demotion.Domain.Auth.Controller;

import com.example.Demotion.Domain.Auth.Config.JwtUtil;
import com.example.Demotion.Domain.Auth.Dto.LoginRequestDto;
import com.example.Demotion.Domain.Auth.Dto.LoginResponseDto;
import com.example.Demotion.Domain.Auth.Dto.SignupRequestDto;
import com.example.Demotion.Domain.Auth.Dto.TokenRequestDto;
import com.example.Demotion.Domain.Auth.Entity.RefreshToken;
import com.example.Demotion.Domain.Auth.Repository.RefreshTokenRepository;
import com.example.Demotion.Domain.Auth.Service.CustomUserDetailService;
import com.example.Demotion.Domain.Auth.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CustomUserDetailService userDetailsService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    // Post : 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody SignupRequestDto request) {
        try {
            userService.registerUser(request.getEmail(), request.getPassword());
            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                return ResponseEntity.status(401).body("비밀번호가 일치하지 않습니다.");
            }

            String accessToken = jwtUtil.generateToken(userDetails.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

            // 기존 refresh 토큰 제거 후 새로운 토큰 저장
            refreshTokenRepository.deleteByEmail(userDetails.getUsername());
            refreshTokenRepository.save(new RefreshToken(null, userDetails.getUsername(), refreshToken));

            return ResponseEntity.ok().body(new LoginResponseDto(
                    "Bearer " + accessToken,
                    refreshToken
            ));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRequestDto request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않은 Refresh Token입니다.");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        RefreshToken savedToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("저장된 Refresh Token이 없습니다."));

        if (!savedToken.getToken().equals(refreshToken)) {
            return ResponseEntity.status(401).body("Refresh Token이 일치하지 않습니다.");
        }

        String newAccessToken = jwtUtil.generateToken(email);
        return ResponseEntity.ok().body(new LoginResponseDto("Bearer " + newAccessToken, refreshToken));
    }
}
