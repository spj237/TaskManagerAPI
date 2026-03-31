package com.example.TaskManagement.Entities;

import com.example.TaskManagement.Enum.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder

public class Users {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> role;
    private String emailOtp;
    private Boolean isEnabled;
    private LocalDateTime otpExpiry;
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "user",cascade ={CascadeType.PERSIST,CascadeType.MERGE})
    private List<UpdatedAt> updatedAt=new ArrayList<>();
    private Boolean isAccountNotLocked=true;
    private Boolean allowNotifications=true;
    private String resetToken;
    private LocalDateTime tokenExpiry;
    private String pictureName;
    private String pictureUrl;
    @OneToMany(mappedBy = "user",cascade =CascadeType.ALL,orphanRemoval = true)
    private List<Task> taskList;
}
