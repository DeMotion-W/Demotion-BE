package com.example.Demotion.Domain.Auth.Service;

import com.example.Demotion.Domain.Auth.Config.JwtUtil;
import com.example.Demotion.Domain.Auth.Dto.LoginResponseDto;
import com.example.Demotion.Domain.Auth.Entity.EmailVerificationCode;
import com.example.Demotion.Domain.Auth.Entity.RefreshToken;
import com.example.Demotion.Domain.Auth.Entity.User;
import com.example.Demotion.Domain.Auth.Repository.EmailVerificationCodeRepository;
import com.example.Demotion.Domain.Auth.Repository.RefreshTokenRepository;
import com.example.Demotion.Domain.Auth.Repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomUserDetailService userDetailsService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationCodeRepository codeRepository;

    // 회원가입
    public Long registerUser(String email, String name, String password) {

        // 이메일 중복 체크
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 저장
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .build();

        return userRepository.save(user).getId();
    }

    // 로그인
    public LoginResponseDto login(String email, String password, HttpServletResponse response) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        // 기존 refresh 토큰 제거 후 새로운 토큰 저장
        refreshTokenRepository.deleteByEmail(userDetails.getUsername());
        refreshTokenRepository.save(RefreshToken.builder()
                .email(userDetails.getUsername())
                .refresh(refreshToken)
                .build());

        // Refresh Token을 HttpOnly 쿠키에 저장
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        return new LoginResponseDto("Bearer " + accessToken, null);
    }

    // 토큰 갱신
    public LoginResponseDto refreshAccessToken(String refreshToken) {
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            throw new JwtException("유효하지 않은 Refresh Token입니다.");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        RefreshToken savedToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("저장된 Refresh Token이 없습니다."));

        if (!savedToken.getRefresh().equals(refreshToken)) {
            throw new JwtException("Refresh Token이 일치하지 않습니다.");
        }

        String newAccessToken = jwtUtil.generateToken(email);
        return new LoginResponseDto("Bearer " + newAccessToken, null);
    }

    // 로그아웃
    public void logout(String accessToken, HttpServletResponse response) {
        String email = jwtUtil.getEmailFromToken(accessToken);

        refreshTokenRepository.deleteByEmail(email);

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    // 비밀번호 재설정
    public void resetPassword(String email, String code, String newPassword) {
        EmailVerificationCode verification = codeRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("인증번호를 찾을 수 없습니다."));

        if (!verification.getCode().equals(code)) {
            throw new IllegalArgumentException("인증번호가 올바르지 않습니다.");
        }

        if (verification.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
