package com.example.TaskManagement.Controller;

import com.example.TaskManagement.Services.UserServices;
import com.example.TaskManagement.dtos.Api.ApiResponse;
import com.example.TaskManagement.dtos.user.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class LoginController {
    private final UserServices userServices;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginIn(@Valid @RequestBody LoginDTO logInfo){
        return ResponseEntity.ok(userServices.login(logInfo));
    }
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDTO>> signup(@RequestBody @Valid UserCreateDTO userCreateDTO){
        ApiResponse<UserResponseDTO> userResponseDTOApiResponse=userServices.register(userCreateDTO);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(userResponseDTOApiResponse);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        String resetUrl = "http://localhost:8080/auth/reset-password";
        userServices.sendResetPasswordEmail(email, resetUrl);
        return ResponseEntity.ok("Email de réinitialisation envoyé");
    }

    // Étape 2 : reset réel
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String newPassword) {
        userServices.resetPassword(token, newPassword);
        return ResponseEntity.ok("password reinitialized");
    }
    @PostMapping("/validateOtp")
    public ResponseEntity<String> validateOtp(@RequestBody @Valid OtpValidationDTO otpValidationDTO){
        userServices.otpValidation(otpValidationDTO);

        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body("otp validated");
    }
    @PostMapping("/sendNewOtp")
    public ResponseEntity<ApiResponse<String>> sendNewOtp(@RequestBody  String email){
       return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(userServices.sendNewOtp(email));
    }
}
