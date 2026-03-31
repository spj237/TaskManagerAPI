package com.example.TaskManagement.Repositories;

import com.example.TaskManagement.Entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Long> {
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<Users> findByEmail(String Email);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmailOtp(String otp);
    List<Users> findAllByIsEnabled(Boolean value);
    List<Users> findAllByIsAccountNotLocked(Boolean value);
    Optional<Users>  findByResetToken(String token);
}
