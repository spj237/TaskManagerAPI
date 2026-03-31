package com.example.TaskManagement.dtos.task;

import com.example.TaskManagement.Enum.Priority;
import com.example.TaskManagement.Enum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskSummaryDTO {
    private Long id;
    private String title;
    private String description;

}
