package com.example.TaskManagement.Services;

import com.example.TaskManagement.Entities.Task;
import com.example.TaskManagement.Repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class NotificationService {
    private final EmailService emailService;
    private final TaskRepository taskRepository;
    private final TaskService taskService;

    @Scheduled(fixedRate = 60000) // 1000 ms = 1 second
    public void sendTaskReminders() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {
            if (task.getReminderDate() == null) {
                continue; // skip this task
            }
            LocalDateTime reminderTime = task.getReminderDate().truncatedTo(ChronoUnit.MINUTES);

            if (!task.isReminderSent() &&
                    !reminderTime.isAfter(now) ) {
                if (task.getUser().getAllowNotifications()) {
                    emailService.sendMessage(
                            task.getUser().getEmail(),
                            "Task Reminder: " + task.getTitle(),
                            "Reminder for your task: " + task.getTitle()
                    );
                    task.setReminderSent(true);
                    taskRepository.save(task);
                }

            }

        }
    }
}
