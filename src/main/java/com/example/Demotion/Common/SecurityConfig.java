package com.example.Demotion.Common;

import com.example.Demotion.Domain.Auth.Config.JwtAuthenticationEntryPoint;
import com.example.Demotion.Domain.Auth.Config.JwtAuthenticationFilter;
import com.example.Demotion.Domain.Auth.Service.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig{

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailServiceImpl userDetailService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("🔐 SecurityFilterChain initialized - configuring security rules");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                        "/api/auth/signup",
                                        "/api/auth/login",
                                        "/api/auth/verify-email/request",
                                        "/api/auth/verify-email/confirm",
                                        "/api/auth/login-refresh",
                                        "/api/auth/reset-password",
                                        "/api/embed/**",
                                        "/api/public/**",
                                        "/api/slack/oauth/callback"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("✅ Security filter chain successfully built");

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // 쿠키 필요한 요청용 설정 (e.g. 인증된 요청, refresh 토큰 등)
        CorsConfiguration securedConfig = new CorsConfiguration();
        securedConfig.setAllowedOrigins(List.of(
                "chrome-extension://gacolobcbkjjijkdnheifekgijfocbda",
                "chrome-extension://dljcpemceaokkcgaofiadhlllbpifpmf",
                "http://localhost:3000",
                "https://demotion-fe.vercel.app"
        ));
        securedConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        securedConfig.setAllowedHeaders(List.of("*"));
        securedConfig.setAllowCredentials(true); // 쿠키 허용

        // 인증도 쿠키도 필요 없는 요청용 설정
        CorsConfiguration publicConfig = new CorsConfiguration();
        publicConfig.setAllowedOrigins(List.of(
                "chrome-extension://gacolobcbkjjijkdnheifekgijfocbda",
                "chrome-extension://dljcpemceaokkcgaofiadhlllbpifpmf",
                "http://localhost:3000",
                "https://demotion-fe.vercel.app"
        ));
        publicConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        publicConfig.setAllowedHeaders(List.of("*"));
        publicConfig.setAllowCredentials(false); // 쿠키 불필요

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 인증 & 쿠키 모두 필요 없는 API 경로
        source.registerCorsConfiguration("/api/auth/signup", publicConfig);
        source.registerCorsConfiguration("/api/auth/login", publicConfig);
        source.registerCorsConfiguration("/api/auth/verify-email/request", publicConfig);
        source.registerCorsConfiguration("/api/auth/verify-email/confirm", publicConfig);
        source.registerCorsConfiguration("/api/auth/reset-password", publicConfig);
        source.registerCorsConfiguration("/api/embed/**", publicConfig);
        source.registerCorsConfiguration("/api/public/**", publicConfig);

        //  Refresh 토큰 API → 인증은 필요 없지만 쿠키 필요
        source.registerCorsConfiguration("/api/auth/login-refresh", securedConfig); // 여기만 securedConfig

        // 그 외는 전부 인증 & 쿠키 필요
        source.registerCorsConfiguration("/**", securedConfig);

        return source;
    }

}