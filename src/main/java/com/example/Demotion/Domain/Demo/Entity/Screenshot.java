package com.example.Demotion.Domain.Demo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Screenshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileUrl; // 객체 URL
    private String buttonText; // 버튼 텍스트
    private String buttonBgColor; // 버튼 색상
    private String buttonTextColor; // 버튼 텍스트 색상
    private String buttonStyle; // 버튼 유형
    private float positionX; // 포인터 x좌표
    private float positionY; // 포인터 x좌표
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demo_id", foreignKey = @ForeignKey(name = "fk_screenshot_demo"))
    private Demo demo;

}
