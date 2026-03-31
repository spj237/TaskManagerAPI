package com.example.TaskManagement.Services;

import com.example.TaskManagement.Entities.BlackList;
import com.example.TaskManagement.Entities.UpdatedAt;
import com.example.TaskManagement.Entities.Users;
import com.example.TaskManagement.Enum.Role;
import com.example.TaskManagement.Exceptions.EmailAlreadyUsedException;
import com.example.TaskManagement.Exceptions.UserNotFoundException;
import com.example.TaskManagement.Exceptions.UsernameAlreadyUsedException;
import com.example.TaskManagement.Mapper.UserMapper;
import com.example.TaskManagement.Repositories.BlackListRepository;
import com.example.TaskManagement.Repositories.TaskRepository;
import com.example.TaskManagement.Repositories.UpdatesRepository;
import com.example.TaskManagement.Repositories.UserRepository;
import com.example.TaskManagement.dtos.Api.ApiResponse;
import com.example.TaskManagement.dtos.updates.UpdateResponseDTO;
import com.example.TaskManagement.dtos.user.*;
import com.example.TaskManagement.security.CustomUserDetails;
import com.example.TaskManagement.security.JwtUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Validated
@Service
public class UserServices {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final long OTP_VALIDATION_EXPIRY = 5;
    private final EmailService emailService;
    private final FileService fileService;
    private final UpdatesRepository updatesRepository;

    private final BlackListRepository blackListRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    @Value("${poster}")
    private String path;
    @Value("${baseUrl}")
    private String baseUrl;

    public UserServices(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, EmailService notificationService, FileService fileService, UpdatesRepository updatesRepository, BlackListRepository blackListRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = notificationService;
        this.fileService = fileService;
        this.updatesRepository = updatesRepository;
        this.blackListRepository = blackListRepository;

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    //registering a new user
    public ApiResponse<LoginResponseDTO> login(LoginDTO logInfo) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logInfo.getUsername(), logInfo.getPassword()));
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        return new ApiResponse<>(200, "login successfully", new LoginResponseDTO(token));
    }

    public ApiResponse<UserResponseDTO> register(UserCreateDTO userCreateDTO) {
        if (userRepository.existsByUsername(userCreateDTO.getUsername())) {
            throw new UsernameAlreadyUsedException("sorry but this user is already in use");
        }
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new EmailAlreadyUsedException("sorry but this email is already in use");
        }
        Set<Role> roleSet = new HashSet<>(List.of(Role.USER));
        Users user = new Users();
        user.setIsEnabled(false);
        user.setUsername(userCreateDTO.getUsername());
        user.setEmail(userCreateDTO.getEmail());

        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));
        user.setEmailOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_VALIDATION_EXPIRY));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(roleSet);
        user.setIsAccountNotLocked(true);
        Users registeredUser = userRepository.save(user);
        emailService.sendMessage(registeredUser.getEmail(), "otp code", user.getEmailOtp());
        return new ApiResponse<>(201, "check email for otp code, user created but not active ", userMapper.toUserResponseDto(registeredUser));

    }

//otp validation and sending code

    public void otpValidation(OtpValidationDTO otpValidationDTO) {
        Users user = userRepository.findByEmail(otpValidationDTO.getEmail()).orElseThrow(() -> new UserNotFoundException("no user with email " + otpValidationDTO.getEmail()));
        if (user.getEmailOtp() == null || !user.getEmailOtp().equals(otpValidationDTO.getOtp())) {
            throw new IllegalArgumentException("invalid OTP");
        }
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP expired");
        }
        user.setIsEnabled(true);

        user.setEmailOtp(null);
        user.setOtpExpiry(null);
        user.getUpdatedAt().add(new UpdatedAt(null, LocalDateTime.now(),
                "Account has been enabled", user));
        Users registeredUser = userRepository.save(user);

        emailService.sendMessage(user.getEmail(), "validation of email", "account created successfully");

    }

    public ApiResponse<UserResponseDTO> otpValForEmailChange(Long userId, OtpValidationDTO otpValidationDTO) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("no user with id " + userId));
        if (user.getEmailOtp() == null || !user.getEmailOtp().equals(otpValidationDTO.getOtp())) {
            throw new IllegalArgumentException("invalid OTP hence email cannot be changed");
        }
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP expired hence email cannot be changed");
        }
        user.setEmail(otpValidationDTO.getEmail());
        user.setEmailOtp(null);
        user.setOtpExpiry(null);
        user.getUpdatedAt().add(new UpdatedAt(null, LocalDateTime.now(),
                "Account has been enabled", user));
        Users registeredUser = userRepository.save(user);
        if (!user.getAllowNotifications()) {
            return new ApiResponse<>(400, "notifications not enabled",  userMapper.toUserResponseDto(user));
        }
        emailService.sendMessage(user.getEmail(), "validation of email", "email address change successfully");
        return new ApiResponse<>(200, "email address change successfully", userMapper.toUserResponseDto(user));
    }

    public ApiResponse<String> sendNewOtp(@NotBlank(message = "email can not be empty") String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("no user with email " + email));
        String otp = String.valueOf((Math.random() * 900000) + 100000);
        user.setEmailOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_VALIDATION_EXPIRY));
        Users registeredUser = userRepository.save(user);
        if (!user.getAllowNotifications()) {
            return new ApiResponse<>(400, "notifications not enabled", "please enable notifications");
        }
        emailService.sendMessage(registeredUser.getEmail(), "send otp", user.getEmailOtp());

        return new ApiResponse<>(201, "check email for otp code", "");

    }

    //    uploading a profile pic
    public String uploadProfilePic(Long userId, MultipartFile file) throws IOException {
        Users user = getUserEntityById(userId);
        String filename = fileService.upoadFile(path, file);
        user.setPictureName(filename);
        user.setPictureUrl(baseUrl + "/Api/user/loadPP/" + filename);
        user.getUpdatedAt().add(new UpdatedAt(null, LocalDateTime.now(), "user uploaded pp", user));
        userRepository.save(user);
        return filename;
    }

    public ApiResponse<LoadPPResponse> loadPP(Long userId, String filename) throws FileNotFoundException {
        Users user = getUserEntityById(userId);
        return new ApiResponse<>(200, "picture loaded", new LoadPPResponse(user.getUsername(), user.getPictureUrl()));
    }

    public Users getUserEntityById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("no user found or user not authenticated"));
    }

    public ApiResponse<UserResponseDTO> getUserById(Long id) {
        Users user = getUserEntityById(id);
        return new ApiResponse<>(200, "user found", userMapper.toUserResponseDto(user));


    }

    public ApiResponse<UserDTO> getDetailsById(Long id) {
        Users user = getUserEntityById(id);
        return new ApiResponse<>(200, "user details found", userMapper.toUserDTO(user));

    }

    public ApiResponse<List<UserDTO>> getAllUsers() {

        return new ApiResponse<>(200, "list of user found successfully", userRepository.findAll().stream().map(userMapper::toUserDTO).toList());

    }

    //    public UserResponseDTO updateAccount() {
//
//    }
    public ApiResponse<String> changeEmail(Long userId, @NotBlank(message = "new email cannot be empty")
    @Size(max = 254, message = "this email is too long 254 characters at most")
    @Email(message = "enter a valid email")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email must look like name@example.com")
    String newEmail) {
        Users user = getUserEntityById(userId);
        Users modifiedUser = null;
        if (!user.getEmail().equals(newEmail)) {
            return sendNewOtp(newEmail);

        }
        return new ApiResponse<>(200, "email didn't change", "");
    }

    public ApiResponse<UserResponseDTO> changeUsername(Long userId, @NotBlank(message = "username can not be empty")
    @Size(max = 30, message = "this username is too long 30 characters at most")
    String username) {
        Users user = getUserEntityById(userId);
        if (!user.getUsername().equals(username)) {
            user.getUpdatedAt().add(new UpdatedAt(null, LocalDateTime.now(), "user change username", user));
        }
        user.setUsername(username);
        Users modifiedUser = userRepository.save(user);

        return new ApiResponse<>(200, "username modified successfully", userMapper.toUserResponseDto(modifiedUser));
    }

    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        Users user = getUserEntityById(userId);
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("this password is not correct");
        }
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmNewPassword())) {
            throw new IllegalArgumentException("the confirmation password must not differ from the new password");
        }
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword())) {
            user.getUpdatedAt().add(new UpdatedAt(null, LocalDateTime.now(), "user changed the password", user));
        }
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        Users modifiedUser = userRepository.save(user);

    }

    public void sendResetPasswordEmail(String email, String resetUrlBase) {

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String token = UUID.randomUUID().toString(); // génération du token unique
        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String link = resetUrlBase + "?token=" + token;
        if (!user.getAllowNotifications()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"user need to enable notifications");
        }
        emailService.sendMessage(user.getEmail(), "reInitialize password", "click this link to reinitialize your password : "+link);

    }


    public void resetPassword(String token, @NotBlank(message = "password can not be empty")
    @Size(min = 8,max=64,message = "password must be at least 8 characters long an at most 64 char long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    ) String newPassword) {
        Users user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("invalid token"));

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expiré");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
    }
    public ApiResponse<List<UpdateResponseDTO>> viewUserUpdatesHistory(Long userId) {
        getUserEntityById(userId);
        return new ApiResponse<>(200, "list of user updates found", updatesRepository.findByUserId(userId).stream().map(updatedAt -> new UpdateResponseDTO(updatedAt.getUser().getUsername(), updatedAt.getUpdatedDate(), updatedAt.getComment())).toList());


    }

    public ApiResponse<List<UserDTO>> getAllBlockedOrNonBlockedUsers(Long userId, Boolean value) {
        getUserEntityById(userId);
        List<UserDTO> userDTOList = userRepository.findAllByIsAccountNotLocked(value).stream().map(userMapper::toUserDTO).toList();
        if (userDTOList.isEmpty()) {
            return new ApiResponse<>(200, "this list is empty", userDTOList);

        }
        return new ApiResponse<>(200, " list found", userDTOList);

    }

    public ApiResponse<List<UserDTO>> getAllActiveOrNonActiveUsers(Long userId, Boolean value) {
        getUserEntityById(userId);
        List<UserDTO> userDTOList = userRepository.findAllByIsEnabled(value).stream().map(userMapper::toUserDTO).toList();
        if (userDTOList.isEmpty()) {
            return new ApiResponse<>(200, "this list is empty", userDTOList);

        }
        return new ApiResponse<>(200, " list  found", userDTOList);

    }

    public ApiResponse<UserDTO> blockOrUnLockUserAccount(Long id, boolean value) {
        Users user = getUserEntityById(id);
        user.setIsAccountNotLocked(value);
        Users blockedUser = userRepository.save(user);
        if (value) {
            user.getUpdatedAt().add(new UpdatedAt(null, LocalDateTime.now(), "user was unlocked", user));
            userRepository.save(user);
            return new ApiResponse<>(200, "user unlocked successfully", userMapper.toUserDTO(blockedUser));
        }
        user.getUpdatedAt().add(new UpdatedAt(null, LocalDateTime.now(), "user was blocked", user));

        userRepository.save(user);
        return new ApiResponse<>(200, "user blocked successfully", userMapper.toUserDTO(blockedUser));


    }


    public ApiResponse<String> activateOrDisActivateNotification(Long userId, boolean value) {
        Users user = getUserEntityById(userId);
        user.setAllowNotifications(value);

        Users blockedUser = userRepository.save(user);
        if (value) {
            return new ApiResponse<>(200, "notifications activated", "");

        }
        return new ApiResponse<>(200, "notifications disActivated", "");

    }

    public ApiResponse<String> logout(Long userId, String token) {
        Users user = getUserEntityById(userId);
        user.getUpdatedAt().add(new UpdatedAt(null, LocalDateTime.now(), "user logout", user));
        userRepository.save(user);
        blackListRepository.save(new BlackList(null, token));
        return new ApiResponse<>(200, "logout successfully redirect to login  page", "");

    }

    public void deleteAccount(Long id) {
        Users user = getUserEntityById(id);
        updatesRepository.save(new UpdatedAt(null, LocalDateTime.now(), "user account with name " + user.getUsername() + " was deleted", null));

        for (UpdatedAt updatedAt : user.getUpdatedAt()) {
            updatedAt.setUser(null);
            updatesRepository.save(updatedAt);
        }

        userRepository.delete(user);
    }
}
