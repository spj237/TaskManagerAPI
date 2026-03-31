package com.example.TaskManagement.dtos.updates;

import com.example.TaskManagement.Entities.Users;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UpdateResponseDTO {
    private String username;
    private LocalDateTime updatedDate;
    private String comment;

}
