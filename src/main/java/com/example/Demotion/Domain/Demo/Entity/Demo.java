package com.example.Demotion.Domain.Demo.Entity;

import com.example.Demotion.Domain.Auth.Entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Demo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subtitle;
    // 필요하면 색상 변수 추가

    @OneToMany(mappedBy = "demo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Screenshot> screenshots;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
