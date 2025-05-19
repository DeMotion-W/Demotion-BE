package com.example.Demotion.Domain.Insight.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ViewerEvent {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ViewerSession session; // 세션

    private Long screenshotId; // 스크린샷 ID (썸네일은 screenshotId 0)
    private Long timestampMillis; // 버튼 클릭 시각 (EpochMillis)
    private Long stayTimeMillis; // 다음 step 진입 시 계산됨
    private LocalDateTime createdAt = LocalDateTime.now();
}
