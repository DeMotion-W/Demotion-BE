package com.example.Demotion.Common;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig{

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보안 비활성화
                // url별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/signup").permitAll() // 모든 사용자 (비로그인 상태 포함) 접근 허용
                        .anyRequest().authenticated() // auth/signup 제외 다른 요청들 인증 필요. jwt나 세션 기반 로그인 필요
                )
                .formLogin(AbstractHttpConfigurer::disable); // 폼 로그인 비활성화

        return http.build();
    }

    // 비밀번호 암호화 방식 Bean으로 저장하여 다른 클래스에서 사용 가능하도록
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
