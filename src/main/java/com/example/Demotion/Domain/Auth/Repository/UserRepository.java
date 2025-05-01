package com.example.Demotion.Domain.Auth.Repository;

import com.example.Demotion.Domain.Auth.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
