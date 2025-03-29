package com.example.Demotion.Domain.Insight.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long demoId;
    private String sessionId;
    private Boolean finished;
    private LocalDateTime createdAt;
}
