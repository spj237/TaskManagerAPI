package com.example.TaskManagement.Mapper;

import com.example.TaskManagement.Entities.Users;
import com.example.TaskManagement.dtos.task.TaskResponseDTO;
import com.example.TaskManagement.dtos.user.UserDTO;
import com.example.TaskManagement.dtos.user.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDTO toUserResponseDto(Users user){
        return new UserResponseDTO(user.getId(),user.getUsername(),user.getEmail(),user.getPictureName(),user.getPictureUrl());
    }
    public UserDTO toUserDTO(Users user){
        return new UserDTO(user.getId(),user.getUsername(),user.getEmail(),user.getRole(),user.getCreatedAt(),user.getIsEnabled(),user.getIsAccountNotLocked());
    }
}
