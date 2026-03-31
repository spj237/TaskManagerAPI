package com.example.TaskManagement.Repositories;

import com.example.TaskManagement.Entities.BlackList;
import com.example.TaskManagement.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListRepository extends JpaRepository<BlackList,Long> {
    Optional<BlackList> findByToken(String token);
    Boolean existsByToken(String token);
}
