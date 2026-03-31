package com.example.TaskManagement.Services;

import com.example.TaskManagement.Entities.Category;
import com.example.TaskManagement.Enum.Priority;
import com.example.TaskManagement.Enum.Status;
import com.example.TaskManagement.dtos.Api.ApiResponse;
import com.example.TaskManagement.Entities.Task;
import com.example.TaskManagement.Entities.UpdatedAt;
import com.example.TaskManagement.Entities.Users;
import com.example.TaskManagement.Exceptions.TaskNotFoundException;
import com.example.TaskManagement.Mapper.TaskMapper;
import com.example.TaskManagement.Repositories.TaskRepository;
import com.example.TaskManagement.Repositories.UpdatesRepository;
import com.example.TaskManagement.Repositories.UserRepository;
import com.example.TaskManagement.dtos.task.TaskCreateDTO;
import com.example.TaskManagement.dtos.task.TaskResponseDTO;
import com.example.TaskManagement.dtos.task.TaskSummaryDTO;
import com.example.TaskManagement.dtos.task.TaskUpdateDTO;
import com.example.TaskManagement.dtos.user.UserStatsDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TaskService {
    private final UserServices userServices;
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final UpdatesRepository updatesRepository;

    public TaskService(UserServices userServices, TaskMapper taskMapper, TaskRepository taskRepository, CategoryService categoryService, UserRepository userRepository, UpdatesRepository updatesRepository) {
        this.userServices = userServices;
        this.taskMapper = taskMapper;
        this.taskRepository = taskRepository;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
        this.updatesRepository = updatesRepository;
    }

    public ApiResponse<TaskResponseDTO> createTask(Long userId, TaskCreateDTO taskCreateDTO) {
        Users user = userServices.getUserEntityById(userId);
        Task task = new Task();
        task.setDescription(taskCreateDTO.getDescription());
        task.setCreatedAt(taskCreateDTO.getCreatedAt());
        task.setStatus(taskCreateDTO.getStatus());
        task.setPriority(taskCreateDTO.getPriority());
        LocalDateTime reminderDate = null;
        long minutes;
        if (taskCreateDTO.getDueDate() == null) {
            taskCreateDTO.setReminderDaysBeforeDue(0);
            taskCreateDTO.setReminderHoursBeforeDue(0);
            taskCreateDTO.setReminderMinutesBeforeDue(0);

        } else {
            if (taskCreateDTO.getDueDate().isBefore(taskCreateDTO.getCreatedAt())) {
                throw new IllegalArgumentException("the due date cannot be before the task creation date");
            }

            if (taskCreateDTO.getReminderDaysBeforeDue() == 0 && taskCreateDTO.getReminderMinutesBeforeDue() == 0 && taskCreateDTO.getReminderHoursBeforeDue() == 0) {
                minutes = ChronoUnit.MINUTES.between(taskCreateDTO.getCreatedAt(), taskCreateDTO.getDueDate());
                long reminderMinutes = minutes / 2;
                reminderDate = taskCreateDTO.getDueDate().minusMinutes(reminderMinutes);
            } else {
                reminderDate = taskCreateDTO.getDueDate().minusDays(taskCreateDTO.getReminderDaysBeforeDue()).minusHours(taskCreateDTO.getReminderHoursBeforeDue()).minusMinutes(taskCreateDTO.getReminderMinutesBeforeDue());
            }
            if (reminderDate.isAfter(taskCreateDTO.getDueDate())) {
                throw new IllegalArgumentException("the reminder date cannot be after the task due date");
            }
            if (reminderDate.isBefore(taskCreateDTO.getCreatedAt())) {
                throw new IllegalArgumentException("the reminder date cannot be before the task creation date");
            }
        }
        String[] wordsPresent = taskCreateDTO.getDescription().split("\\s+");
        int maxWords = 6;
        int wordToUse = Math.min(maxWords, wordsPresent.length);

        StringBuilder titleBuilder = new StringBuilder();
        for (int i = 0; i < wordToUse; i++) {
            titleBuilder.append(wordsPresent[i]);
            if (i < wordToUse - 1) {
                titleBuilder.append(" ");
            }

        }
        String dot = wordToUse < wordsPresent.length ? "..." : "";
        task.setTitle(titleBuilder.toString() + dot);

        task.setReminderDate(reminderDate);
        task.setDueDate(taskCreateDTO.getDueDate());
        Category category = categoryService.findCategoryByNameOrCreate(taskCreateDTO.getCategoryName());
        task.setCategory(category);
        task.setUser(user);
        user.getTaskList().add(task);
        UpdatedAt updatedAt = new UpdatedAt(null, LocalDateTime.now(), "created a new task titled " + task.getTitle(), user);
        user.getUpdatedAt().add(updatedAt);
        category.getTaskList().add(task);
        Task createdTask = taskRepository.save(task);

        updatesRepository.save(updatedAt);
        return new ApiResponse<>(201, "task created  successfully", taskMapper.toTaskResponseDto(createdTask));


    }

    public Task getTaskEntityById(Long userId, Long id) {
        return taskRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new TaskNotFoundException("task with id " + id + " was not found attached to this user"));
    }

    public ApiResponse<TaskSummaryDTO> getTaskById(Long userId, Long id) {
        userServices.getUserEntityById(userId);
        Task task = getTaskEntityById(id, userId);
        return new ApiResponse<>(200, "task found", new TaskSummaryDTO(task.getId(), task.getTitle(), task.getDescription()));
    }

    public ApiResponse<TaskResponseDTO> getTaskDetailsById(Long userId, Long id) {
        userServices.getUserEntityById(userId);
        Task task = getTaskEntityById(id, userId);
        return new ApiResponse<>(200, "task details found", taskMapper.toTaskResponseDto(task));


    }

    public ApiResponse<List<TaskSummaryDTO>> getAllTasks(Long userId) {
        userServices.getUserEntityById(userId);
        List<TaskSummaryDTO> taskSummaryDTOS = taskRepository.findAllByUserId(userId).stream().map(task -> new TaskSummaryDTO(task.getId(), task.getTitle(), task.getDescription())).toList();
        if (taskSummaryDTOS.isEmpty()) {
            return new ApiResponse<>(200, "no task yet", taskSummaryDTOS);

        }
        return new ApiResponse<>(200, "list of task found", taskSummaryDTOS);
    }

    public ApiResponse<List<TaskSummaryDTO>> filterTasksByCategory(Long userId, String categoryName) {
        userServices.getUserEntityById(userId);
        categoryService.getCategoryEntityByName(categoryName);
        List<TaskSummaryDTO> taskSummaryDTOS = taskRepository.findAllByCategoryNameAndUserId(categoryName, userId).stream().map(task -> new TaskSummaryDTO(task.getId(), task.getTitle(), task.getDescription())).toList();
        if (taskSummaryDTOS.isEmpty()) {
            return new ApiResponse<>(200, "no task with this category yet", taskSummaryDTOS);

        }
        return new ApiResponse<>(200, "list of task found", taskSummaryDTOS);
    }

    public ApiResponse<List<TaskSummaryDTO>> filterTasksByPriority(Long userId, Priority priority) {
        userServices.getUserEntityById(userId);
        List<TaskSummaryDTO> taskSummaryDTOS = taskRepository.findAllByPriorityAndUserId(priority, userId).stream().map(task -> new TaskSummaryDTO(task.getId(), task.getTitle(), task.getDescription())).toList();
        if (taskSummaryDTOS.isEmpty()) {
            return new ApiResponse<>(200, "no task with this priority level yet", taskSummaryDTOS);

        }
        return new ApiResponse<>(200, "list of task found", taskSummaryDTOS);
    }

    public ApiResponse<List<TaskSummaryDTO>> filterTasksByStatus(Long userId, Status status) {
        userServices.getUserEntityById(userId);
        List<TaskSummaryDTO> taskSummaryDTOS = taskRepository.findAllByStatusAndUserId(status, userId).stream().map(task -> new TaskSummaryDTO(task.getId(), task.getTitle(), task.getDescription())).toList();
        if (taskSummaryDTOS.isEmpty()) {
            return new ApiResponse<>(200, "no task with this status yet", taskSummaryDTOS);

        }
        return new ApiResponse<>(200, "list of task found", taskSummaryDTOS);
    }

    public ApiResponse<TaskResponseDTO> updateTask(Long userId, Long taskId, TaskUpdateDTO taskUpdateDTO) {
        Users user = userServices.getUserEntityById(userId);
        Task task = getTaskEntityById(userId, taskId);
        if (taskUpdateDTO.getDescription() != null) {
            if (taskUpdateDTO.getDescription().isBlank()) {
                throw new IllegalArgumentException("Description cannot be empty");
            }
            task.setDescription(taskUpdateDTO.getDescription());
        }
        if (taskUpdateDTO.getTitle() != null) {
            task.setTitle(taskUpdateDTO.getTitle());
        }
        if (taskUpdateDTO.getCategoryName() != null) {
            task.setCategory(categoryService.findCategoryByNameOrCreate(taskUpdateDTO.getCategoryName()));

        }
        if (taskUpdateDTO.getDescription() != null && taskUpdateDTO.getDescription().isBlank()) {
            throw new IllegalArgumentException("cannot set empty description");
        }
        if (taskUpdateDTO.getDescription() != null && !taskUpdateDTO.getDescription().isBlank()) {
            task.setDescription(taskUpdateDTO.getDescription());
        }

        if (taskUpdateDTO.getStatus() != null) {
            task.setStatus(taskUpdateDTO.getStatus());
        }

        if (taskUpdateDTO.getPriority() != null) {
            task.setPriority(taskUpdateDTO.getPriority());
        }
        if (taskUpdateDTO.getCreatedAt() != null) {
            task.setCreatedAt(taskUpdateDTO.getCreatedAt());
        }
        LocalDateTime reminderDate = null;
        long minutes;
        if (taskUpdateDTO.getCreatedAt() != null) {
            taskUpdateDTO.setCreatedAt(task.getCreatedAt());
        }
        if (taskUpdateDTO.getDueDate() != null) {
            if (taskUpdateDTO.getCreatedAt() == null) {
                taskUpdateDTO.setCreatedAt(task.getCreatedAt());
            }

            task.setDueDate(taskUpdateDTO.getDueDate());
            if (taskUpdateDTO.getReminderDaysBeforeDue() == 0 && taskUpdateDTO.getReminderMinutesBeforeDue() == 0 && taskUpdateDTO.getReminderHoursBeforeDue() == 0) {
                minutes = ChronoUnit.MINUTES.between(taskUpdateDTO.getCreatedAt(), taskUpdateDTO.getDueDate());
                long reminderMinutes = minutes / 2;
                reminderDate = taskUpdateDTO.getDueDate().minusMinutes(reminderMinutes);
            } else {
                reminderDate = taskUpdateDTO.getDueDate().minusDays(taskUpdateDTO.getReminderDaysBeforeDue()).minusHours(taskUpdateDTO.getReminderHoursBeforeDue()).minusMinutes(taskUpdateDTO.getReminderMinutesBeforeDue());
            }
            if (reminderDate.isAfter(taskUpdateDTO.getDueDate())) {
                throw new IllegalArgumentException("the reminder date cannot be after the task due date");
            }
            if (reminderDate.isBefore(taskUpdateDTO.getCreatedAt())) {
                throw new IllegalArgumentException("the reminder date cannot be before the task creation date");
            }
        }
        user.getTaskList().add(task);
        task.setUser(user);
        Task updatedTask = taskRepository.save(task);
        UpdatedAt updatedAt = new UpdatedAt(null, LocalDateTime.now(), "updated a task", user);
        if (taskUpdateDTO.getStatus() != null) {
            if (taskUpdateDTO.getStatus().equals(Status.DONE)) {
                updatedAt = new UpdatedAt(null, LocalDateTime.now(), "user done with task " + task.getTitle(), user);
            }
        }

        user.getUpdatedAt().add(updatedAt);
        updatesRepository.save(updatedAt);
        return new ApiResponse<>(200, "task updated successfully", taskMapper.toTaskResponseDto(updatedTask));

    }

    public ApiResponse<UserStatsDTO> getStatsForUser(Long userId) {
        userServices.getUserEntityById(userId);
        long total = taskRepository.countByUserId(userId);
        long pending = taskRepository.countByUserIdAndStatus(userId, Status.IN_PROGRESS);
        long completed = taskRepository.countByUserIdAndStatus(userId, Status.DONE);
        return new ApiResponse<>(200, "found", new UserStatsDTO(userId, total, pending, completed));


    }

    public ApiResponse<List<UserStatsDTO>> getStatsForAllUsers() {
        List<UserStatsDTO> userStatsDTOList = userRepository.findAll().stream()
                .map(user -> {
                    Long userId = user.getId();
                    long total = taskRepository.countByUserId(userId);
                    long pending = taskRepository.countByUserIdAndStatus(userId, Status.IN_PROGRESS);
                    long completed = taskRepository.countByUserIdAndStatus(userId, Status.DONE);
                    return new UserStatsDTO(userId, total, pending, completed);
                })
                .toList();
        return new ApiResponse<>(200, "found", userStatsDTOList);

    }

    public void deleteTask(Long userId, Long taskId) {
        Users user = userServices.getUserEntityById(userId);
        Task task = getTaskEntityById(userId, taskId);
        taskRepository.deleteById(taskId);
        UpdatedAt updatedAt = new UpdatedAt(null, LocalDateTime.now(), "deleted a task titled " + task.getTitle(), user);
        user.getUpdatedAt().add(updatedAt);
        updatesRepository.save(updatedAt);
    }

}
