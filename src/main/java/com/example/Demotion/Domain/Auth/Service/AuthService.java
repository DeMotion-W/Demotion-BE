package com.example.Demotion.Domain.Auth.Service;

import com.example.Demotion.Common.ErrorCode;
import com.example.Demotion.Common.ErrorDomain;
import com.example.Demotion.Domain.Auth.Config.JwtUtil;
import com.example.Demotion.Domain.Auth.Dto.AccessTokenResponseDto;
import com.example.Demotion.Domain.Auth.Dto.LoginResponseDto;
import com.example.Demotion.Domain.Auth.Entity.AccessTokenLog;
import com.example.Demotion.Domain.Auth.Entity.EmailVerificationCode;
import com.example.Demotion.Domain.Auth.Entity.RefreshToken;
import com.example.Demotion.Domain.Auth.Entity.User;
import com.example.Demotion.Domain.Auth.Repository.AccessTokenLogRepository;
import com.example.Demotion.Domain.Auth.Repository.EmailVerificationCodeRepository;
import com.example.Demotion.Domain.Auth.Repository.RefreshTokenRepository;
import com.example.Demotion.Domain.Auth.Repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserDetailServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationCodeRepository codeRepository;
    private final AccessTokenLogRepository accessTokenLogRepository;

    public class DuplicateEmailException extends RuntimeException {}

    // 회원가입
    public void registerUser(String email, String name, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ErrorDomain(ErrorCode.DUPLICATED_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .build();

        userRepository.save(user);
    }

    // 로그인
    public LoginResponseDto login(String email, String password, HttpServletResponse response) {
        User user = (User) userDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("비밀번호 불일치");
        }

        String accessToken = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        refreshTokenRepository.deleteByEmail(user.getEmail());
        refreshTokenRepository.save(RefreshToken.builder()
                .email(user.getEmail())
                .refresh(refreshToken)
                .build());

        // 쿠키 수동 구성
        String cookieValue = String.format(
                "refreshToken=%s; Max-Age=%d; Path=/; Secure; HttpOnly; SameSite=None",
                refreshToken,
                7 * 24 * 60 * 60 // 7일
        );
        response.setHeader("Set-Cookie", cookieValue);

        return new LoginResponseDto("Bearer " + accessToken, user.getId(), user.getName());
    }



    // 토큰 갱신
    public AccessTokenResponseDto refreshAccessToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            throw new ErrorDomain(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        RefreshToken savedToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorDomain(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!savedToken.getRefresh().equals(refreshToken)) {
            throw new ErrorDomain(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 새 토큰 발급
        String newAccessToken = jwtUtil.generateToken(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        // DB 갱신
        savedToken.setRefresh(newRefreshToken);
        refreshTokenRepository.save(savedToken);

        // Set-Cookie: refreshToken
        String cookieValue = String.format(
                "refreshToken=%s; Max-Age=%d; Path=/; Secure; HttpOnly; SameSite=Strict",
                newRefreshToken,
                30 * 24 * 60 * 60
        );
        response.setHeader("Set-Cookie", cookieValue);

        return new AccessTokenResponseDto("Bearer " + newAccessToken);
    }


    // 로그아웃
    public void logout(String accessToken, HttpServletResponse response) {
        String email;
        try {
            email = jwtUtil.getEmailFromToken(accessToken);
        } catch (Exception e) {
            throw new ErrorDomain(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        if (!refreshTokenRepository.existsByEmail(email)) {
            throw new ErrorDomain(ErrorCode.USER_NOT_FOUND);
        }

        refreshTokenRepository.deleteByEmail(email);

        // 쿠키 만료
        String expiredCookie = "refreshToken=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=None";
        response.setHeader("Set-Cookie", expiredCookie);
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
