package com.example.TaskManagement.dtos.updates;

import com.example.TaskManagement.Entities.Users;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class updateDTO {
    private Long id;
    private LocalDateTime updatedDate;
    private String comment;
   private Users user;
}
