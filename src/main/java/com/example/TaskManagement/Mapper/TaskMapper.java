package com.example.TaskManagement.Mapper;

import com.example.TaskManagement.Entities.Task;
import com.example.TaskManagement.dtos.task.TaskDTO;
import com.example.TaskManagement.dtos.task.TaskResponseDTO;
import com.example.TaskManagement.dtos.task.TaskSummaryDTO;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskDTO toTaskDto(Task task){
        return new TaskDTO(task.getId(),task.getTitle(),task.getDescription(),task.getCategory().getId(),task.getStatus(),task.getPriority(),task.getCreatedAt(),task.getReminderDate(),task.getDueDate(),task.getUser().getId());
    }
    public TaskResponseDTO toTaskResponseDto(Task task){
        return new TaskResponseDTO(task.getId(),task.getTitle(),task.getDescription(),task.getCategory().getName(),task.getStatus(),task.getPriority(),task.getCreatedAt(),task.getReminderDate(),task.getDueDate());
    }
    public TaskSummaryDTO toTaskSummaryDTO(Task task){
        return new TaskSummaryDTO(task.getId(),task.getTitle(),task.getDescription());
    }
}
