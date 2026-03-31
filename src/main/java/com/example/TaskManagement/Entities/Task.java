package com.example.TaskManagement.Entities;

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
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String title;
    @Column(nullable = false)
    @Lob
    private String description;
    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)

    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime reminderDate;
    boolean reminderSent = false;
    private LocalDateTime dueDate;
    @ManyToOne
    @JoinColumn(name="user_id")
    private Users user;
}
