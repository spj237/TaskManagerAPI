package com.example.TaskManagement.Repositories;

import com.example.TaskManagement.Entities.Category;
import com.example.TaskManagement.Entities.Task;
import com.example.TaskManagement.Enum.Priority;
import com.example.TaskManagement.Enum.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findAllByCategoryNameAndUserId(String name,Long userId);
    List<Task> findAllByPriorityAndUserId(Priority priority,Long userId);
    List<Task> findAllByStatusAndUserId(Status status,Long userId);
    List<Task> findAllByUserId(Long userId);
    Optional<Task> findByIdAndUserId(Long taskId,Long userId);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, Status status);

}
