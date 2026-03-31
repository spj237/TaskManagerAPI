package com.example.TaskManagement.dtos.category;

import com.example.TaskManagement.dtos.task.TaskSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private List<TaskSummaryDTO> taskSummaryDTOList;
}
