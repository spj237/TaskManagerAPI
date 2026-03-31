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
public class ChangeEmailDTO {
    @NotBlank(message = "new email cannot be empty")
    @Size(max=254,message = "this email is too long 254 characters at most")
    @Email(message = "enter a valid email")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email must look like name@example.com")
    private String newEmail;
//    @NotBlank(message = "old email cannot be empty")

}
