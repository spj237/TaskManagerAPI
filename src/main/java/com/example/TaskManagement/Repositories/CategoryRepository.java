package com.example.TaskManagement.Repositories;

import com.example.TaskManagement.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Optional<Category> findByName(String name);
    Boolean existsByName(String name);
}
