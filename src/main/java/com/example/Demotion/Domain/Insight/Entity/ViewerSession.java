package com.example.Demotion.Domain.Insight.Entity;

import com.example.Demotion.Domain.Demo.Entity.Demo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ViewerSession {

    @Id
    @GeneratedValue
    private Long id;

    private String email; // 이메일
    private boolean contactClicked = false; // cta 클릭 여부
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demo_id", foreignKey = @ForeignKey(name = "fk_viewersession_demo"))
    private Demo demo;

    @OneToMany(mappedBy = "session", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ViewerEvent> viewerEvents = new ArrayList<>();
}
