package com.example.TaskManagement.security;

import com.example.TaskManagement.Entities.Users;
import com.example.TaskManagement.Enum.Role;
import com.example.TaskManagement.Repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        // Check if default admin exists
        if (!userRepository.existsById(1L)) {
            Users admin = new Users();
            admin.setUsername("myName");
            admin.setEmail("admin@example.com");
            admin.setCreatedAt(LocalDateTime.now());
            admin.setPassword(passwordEncoder.encode("Admin123"));
            admin.setIsEnabled(true); // admin is active immediately
            admin.setRole(new HashSet<>(List.of(Role.ADMIN))); // assign enum directly
            userRepository.save(admin);
        }
    }
}
