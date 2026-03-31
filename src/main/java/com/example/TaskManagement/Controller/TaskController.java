package com.example.TaskManagement.Controller;

import com.example.TaskManagement.Enum.Priority;
import com.example.TaskManagement.Enum.Status;
import com.example.TaskManagement.Services.TaskService;
import com.example.TaskManagement.dtos.Api.ApiResponse;
import com.example.TaskManagement.dtos.task.TaskCreateDTO;
import com.example.TaskManagement.dtos.task.TaskResponseDTO;
import com.example.TaskManagement.dtos.task.TaskSummaryDTO;
import com.example.TaskManagement.dtos.task.TaskUpdateDTO;
import com.example.TaskManagement.dtos.user.UserStatsDTO;
import com.example.TaskManagement.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/Api/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> createTask(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid TaskCreateDTO taskCreateDTO){
        ApiResponse<TaskResponseDTO> apiResponse = taskService.createTask(userDetails.getId(), taskCreateDTO);
        return ResponseEntity.created(URI.create("/Api/user/taskDetails/"+apiResponse.getData().getId())).body(apiResponse);
    }
    @GetMapping("/getTask/{id}")
    public ResponseEntity<ApiResponse<TaskSummaryDTO>> getTask(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id){
      return ResponseEntity.ok(taskService.getTaskById(userDetails.getId(),id));
    }
    @GetMapping("/getTaskDetails/{id}")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> getTaskDetails(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id){
        return ResponseEntity.ok(taskService.getTaskDetailsById(userDetails.getId(),id));
    }
    @GetMapping("/getAllTask/me")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getAllTask(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(taskService.getAllTasks(userDetails.getId()));
    }
    @GetMapping("/filterByCat")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> filterByCategory(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody String categoryName){
        return ResponseEntity.ok(taskService.filterTasksByCategory(userDetails.getId(),categoryName));
    }
    @GetMapping("/filterByPriority")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> filterByPriority(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody String priority){

        return ResponseEntity.ok(taskService.filterTasksByPriority(userDetails.getId(),Priority.valueOf(priority.toUpperCase())));
    }
    @GetMapping("/filterByStatus")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> filterByStatus(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody String status){
        return ResponseEntity.ok(taskService.filterTasksByStatus(userDetails.getId(),Status.valueOf(status.toUpperCase())));
    }
    @GetMapping("/getStats/me")
    public ResponseEntity<ApiResponse<UserStatsDTO>> getPersonalStat(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(taskService.getStatsForUser(userDetails.getId()));
    }
    @GetMapping("/getStats/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserStatsDTO>> getUserStat(@PathVariable Long userId){
        return ResponseEntity.ok(taskService.getStatsForUser(userId));
    }
    @GetMapping("/getAllStats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserStatsDTO>>> getAllUsersStat(){
        return ResponseEntity.ok(taskService.getStatsForAllUsers());
    }
    @PatchMapping("/updateTask/{id}")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateTask(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable Long id, @RequestBody @Valid TaskUpdateDTO taskUpdateDTO){
        return ResponseEntity.ok(taskService.updateTask(userDetails.getId(),id ,taskUpdateDTO));
    }
    @DeleteMapping("/deleteTask/{id}")
    public ResponseEntity<String> deleteTask(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable Long id){
        taskService.deleteTask(userDetails.getId(),id);
        return ResponseEntity.ok("deleted");
    }

}
