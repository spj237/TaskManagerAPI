package com.example.TaskManagement.dtos.task;

import com.example.TaskManagement.Enum.Priority;
import com.example.TaskManagement.Enum.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskUpdateDTO {
    @Size(max=200,message = "title can not exceed 200 char")
    private String title;
    @Size(max=2000,message = "this task  is too long")

    private String description;
    @Size(max=200,message = "categoryName can not exceed 200 char")
    private String categoryName="General";
    private Status status;
    private Priority priority;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer reminderDaysBeforeDue=0;
    private Integer reminderHoursBeforeDue=0;
    private Integer reminderMinutesBeforeDue=0;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDate;
}
