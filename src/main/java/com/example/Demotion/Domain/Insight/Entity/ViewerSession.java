package com.example.Demotion.Domain.Insight.Entity;

import com.example.Demotion.Domain.Demo.Entity.Demo;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ViewerSession {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Demo demo; // 조회 데모

    private String email; // 이메일
    private boolean contactClicked = false; // cta 클릭 여부
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일
}
