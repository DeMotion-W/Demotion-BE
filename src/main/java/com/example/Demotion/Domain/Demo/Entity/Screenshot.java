package com.example.Demotion.Domain.Demo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Screenshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "step_order")
    private int order;

    private String fileUrl;
    private String buttonText;
    private String buttonColor;
    private String buttonStyle;
    private float positionX;
    private float positionY;

    @ManyToOne
    @JoinColumn(name = "demo_id")
    private Demo demo;
}
