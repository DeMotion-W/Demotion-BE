package com.example.Demotion.Domain.Demo.Entity;

import com.example.Demotion.Domain.Auth.Entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Demo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 제목
    private String subtitle; // 부제목
    private String buttonColor; // 버튼 색상
    private String buttonTextColor; // 버튼 텍스트 컬러
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일

    // 스크린샷 목록
    @OneToMany(mappedBy = "demo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Screenshot> screenshots;

    // User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
