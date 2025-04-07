package com.example.Demotion.Domain.Insight.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "step_view_log")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StepViewLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long viewLogId;   // ViewLog와 연동 (세션 단위 시청 로그)
    private Long stepId;      // 어떤 스텝인지
    private LocalDateTime viewedAt; // 언제 봤는지
    private Integer duration; // 얼마나 머물렀는지 (초 단위)
}