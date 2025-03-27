package com.example.Demotion.Domain.Auth.Service;

import com.example.Demotion.Domain.Auth.Entity.User;
import com.example.Demotion.Domain.Auth.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // registerUser : 회원가입 서비스
    public Long registerUser(String email, String password) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 저장
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .build();

        return userRepository.save(user).getId();
    }
}
