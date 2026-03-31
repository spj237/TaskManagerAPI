package com.example.TaskManagement.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDTO {
    @NotBlank(message = "username can not be empty")
    @Size(max=30,message = "this username is too long 30 characters at most")
    private String username;
    @NotBlank(message = "email cannot be empty")
    @Size(max=254,message = "this email is too long 254 characters at most")
    @Email(message = "enter a valid email")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email must look like name@example.com")
    private String email;
    @NotBlank(message = "password can not be empty")
    @Size(min = 8,max=64,message = "password must be at least 8 characters long an at most 64 char long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    private String password;

}
