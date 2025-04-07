package com.example.Demotion.Domain.Insight.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "step")
public class Step {
    @Id
    private Long id;

    private Long demoId;

    // 필드와 getter/setter 등 추가
}
