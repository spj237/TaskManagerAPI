package com.example.TaskManagement.dtos.Api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError{
    private int status;
    private List<String> errorMessage=new ArrayList<>();
    private String path;
    private LocalDateTime timestamp=LocalDateTime.now();

    public ApiError(int status, List<String> errorMessage, String path) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.path = path;
    }
}
