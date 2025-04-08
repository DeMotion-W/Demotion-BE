package com.example.Demotion.Domain.Demo.Repository;

import com.example.Demotion.Domain.Demo.Entity.Demo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DemoRepository extends JpaRepository<Demo, Long> {

    // 🎯 embedCode로 Demo 조회하는 메서드
    Optional<Demo> findByEmbedCode(String embedCode);
}
