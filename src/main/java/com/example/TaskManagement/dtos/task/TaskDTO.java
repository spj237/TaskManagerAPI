package com.example.TaskManagement.dtos.task;

import com.example.TaskManagement.Entities.Category;
import com.example.TaskManagement.Entities.Users;
import com.example.TaskManagement.Enum.Priority;
import com.example.TaskManagement.Enum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private Status status;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime reminderDate;
    private LocalDateTime dueDate;
    private Long userId;
}
