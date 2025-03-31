package com.example.Demotion.Domain.Insight.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "page_view_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageViewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long demoId;      // 어떤 데모의
    private int pageNumber;   // 몇 번째 페이지인지
    private LocalDateTime viewedAt; // 조회 시각
}
