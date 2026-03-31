package com.example.TaskManagement.Repositories;

import com.example.TaskManagement.Entities.UpdatedAt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface UpdatesRepository extends JpaRepository<UpdatedAt,Long> {
    List<UpdatedAt> findByUserId(Long id);

}
