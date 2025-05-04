package com.example.Demotion.Domain.Demo.Repository;

import com.example.Demotion.Domain.Demo.Entity.Demo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoRepository extends JpaRepository<Demo, Long> {
}
