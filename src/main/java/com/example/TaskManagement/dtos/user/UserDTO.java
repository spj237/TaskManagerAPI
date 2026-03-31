package com.example.TaskManagement.dtos.user;

import com.example.TaskManagement.Entities.Task;
import com.example.TaskManagement.Enum.Role;
import com.example.TaskManagement.dtos.task.TaskDTO;
import com.example.TaskManagement.dtos.task.TaskResponseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Set<Role> role;
    private LocalDateTime createdAt;
    private Boolean isEnabled;
    private Boolean isAccountNotLocked;

}
