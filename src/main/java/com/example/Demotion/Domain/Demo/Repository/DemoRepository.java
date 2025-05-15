package com.example.Demotion.Domain.Demo.Repository;

import com.example.Demotion.Domain.Demo.Entity.Demo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DemoRepository extends JpaRepository<Demo, Long> {
    List<Demo> findAllByUserId(Long userId);
}
