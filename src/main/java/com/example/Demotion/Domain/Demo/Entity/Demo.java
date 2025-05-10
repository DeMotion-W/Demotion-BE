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

    @Column(unique = true, nullable = false)
    private String publicId;

    private String title;

    private String subtitle;

    @OneToMany(mappedBy = "demo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Screenshot> screenshots;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // @PrePersist -> 엔티티가 처음 저장되기 전에 자동으로 실행되는 JPA 라이프사이클 콜백
    @PrePersist
    protected void onPrePersist() {
        if (this.publicId == null || this.publicId.isEmpty()) {
            this.publicId = UUID.randomUUID().toString();
        }

        this.createdAt = LocalDateTime.now();
    }
}
