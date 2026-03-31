package com.example.TaskManagement.Controller;

import com.example.TaskManagement.Services.FileService;
import com.example.TaskManagement.Services.UserServices;
import com.example.TaskManagement.dtos.Api.ApiResponse;
import com.example.TaskManagement.dtos.updates.UpdateResponseDTO;
import com.example.TaskManagement.dtos.user.*;
import com.example.TaskManagement.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/Api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServices userServices;
    private final FileService fileService;
    @Value("${poster}")
    private String path;
    @GetMapping("/me")
     public ResponseEntity<ApiResponse<UserResponseDTO>> getUserMe(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(userServices.getUserById(userDetails.getId()));
    }
    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserDetails(@PathVariable Long id){
        return ResponseEntity.ok(userServices.getDetailsById(id));
    }
    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(){
        return ResponseEntity.ok(userServices.getAllUsers());
    }
    @PatchMapping("/uploadPP")
    public ResponseEntity<String> uploadPP(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestPart MultipartFile file) throws IOException {
        Long userId= userDetails.getId();
        return ResponseEntity.ok(userServices.uploadProfilePic(userId,file));
    }
//    @PostMapping("/loadPP/{filename}")
//    public ResponseEntity<ApiResponse<LoadPPResponse>> loadPP(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String filename) throws IOException {
//        Long userId= userDetails.getId();
//       return ResponseEntity.ok(userServices.loadPP(userId,filename));
//
//    }
    @GetMapping("/loadPP/{filename}")
    public void getFile(@PathVariable String filename, HttpServletResponse response) throws IOException {
        InputStream bytes = fileService.getResource(path,filename);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(bytes,response.getOutputStream());
    }
   @PatchMapping("/change/email")
    public ResponseEntity<ApiResponse<String>> changeEmail(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody String newEmail){

        return ResponseEntity.ok(userServices.changeEmail(userDetails.getId(),newEmail));
    }
    @PatchMapping("/validateOtp")
    public ResponseEntity<ApiResponse<UserResponseDTO>> validateOtp(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody @Valid OtpValidationDTO otpValidationDTO){
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(userServices.otpValForEmailChange(userDetails.getId(), otpValidationDTO));
    }

    @PatchMapping("/change/username")
    public ResponseEntity<ApiResponse<UserResponseDTO>> changeUsername(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody String username){
        Long userId=userDetails.getId();
        return ResponseEntity.ok(userServices.changeUsername(userId,username));
    }
    @PatchMapping("/change/password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid ChangePasswordDTO changePasswordDTO){
        Long userId=userDetails.getId();
        userServices.changePassword(userId,changePasswordDTO);
        return ResponseEntity.ok("password change successfully");
    }
    @GetMapping("/myUserUpdates")
    ResponseEntity<ApiResponse<List<UpdateResponseDTO>>> viewUserUpdatesMe(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(userServices.viewUserUpdatesHistory(userDetails.getId()));
    }
    @GetMapping("/UserUpdates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<List<UpdateResponseDTO>>> viewUserUpdates(@PathVariable Long id){
        return ResponseEntity.ok(userServices.viewUserUpdatesHistory(id));
    }
    @GetMapping("/getAll/blockedAcct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getBlockedUsers(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId=userDetails.getUser().getId();
        return ResponseEntity.ok(userServices.getAllBlockedOrNonBlockedUsers(userId,false));
    }
    @GetMapping("/getAll/NonBlockedAcct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getNonBlockedUsers(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId=userDetails.getUser().getId();
        return ResponseEntity.ok(userServices.getAllBlockedOrNonBlockedUsers(userId,true));
    }
    @GetMapping("/getAll/activeAcct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getActiveAcct(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId=userDetails.getUser().getId();
        return ResponseEntity.ok(userServices.getAllActiveOrNonActiveUsers(userId,true));
    }
    @GetMapping("/getAll/nonActiveAcct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getActiveOrNonActiveAcct(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId=userDetails.getUser().getId();
        return ResponseEntity.ok(userServices.getAllActiveOrNonActiveUsers(userId,false));
    }
    @PatchMapping("/block/unLock/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<ApiResponse<UserDTO>> blockOrUnLockAcct(@PathVariable(name = "id") Long blockedId, @RequestBody boolean value){

        return ResponseEntity.ok(userServices.blockOrUnLockUserAccount(blockedId,value));
    }
    @PatchMapping("/activate/disActivateNotification/me")
    public
    ResponseEntity
            <ApiResponse<String>> activateOrDisActivateNot(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody boolean value){
           return ResponseEntity.ok(userServices.activateOrDisActivateNotification(userDetails.getId(), value));
    }
    @PostMapping("/logout")
    public
    ResponseEntity
            <ApiResponse<String>> logout(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody String token){
        return ResponseEntity.ok(userServices.logout(userDetails.getId(),token));
    }
    @DeleteMapping("/deleteAcct/me")
    @PreAuthorize("hasRole('USER')")
    public  ResponseEntity<String> deleteAcct(@AuthenticationPrincipal CustomUserDetails userDetails){
        userServices.deleteAccount(userDetails.getId());
        return ResponseEntity.ok("account deleted");
    }
}
