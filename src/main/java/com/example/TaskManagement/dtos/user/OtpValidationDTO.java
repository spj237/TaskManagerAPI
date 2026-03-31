package com.example.TaskManagement.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.parameters.P;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OtpValidationDTO {
    @NotBlank(message = "email can not be empty")
    private String email;
    @NotBlank(message = "this field cannot be empty")
    @Size(min = 6,max = 6,message = "need 6 digits")
    private String otp;
}
