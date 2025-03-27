package com.example.Demotion.Domain.Auth.Controller;

import com.example.Demotion.Domain.Auth.Dto.SignupRequestDto;
import com.example.Demotion.Domain.Auth.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
}
