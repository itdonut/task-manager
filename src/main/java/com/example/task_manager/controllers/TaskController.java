package com.example.task_manager.controllers;

import com.example.task_manager.dtos.request.task.AssignUserRequestDto;
import com.example.task_manager.dtos.request.task.TaskRequestDto;
import com.example.task_manager.dtos.response.task.TaskResponseDto;
import com.example.task_manager.services.ITaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@Validated
public class TaskController {
    private final ITaskService taskService;

    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<TaskResponseDto>> getTasksByUserId(
            @PathVariable
            @NotBlank(message = "User ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getTasksByUserId(id));
    }

    @PostMapping("/user/{id}")
    public ResponseEntity<TaskResponseDto> createTaskForUser(
            @PathVariable
            @NotBlank(message = "User ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id,
            @Valid @RequestBody TaskRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createUserTask(id, dto));
    }

    @PostMapping("/team/{id}")
    public ResponseEntity<TaskResponseDto> createTaskForTeam(
            @PathVariable
            @NotBlank(message = "Team ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id,
            @Valid @RequestBody TaskRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTeamTask(id, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable
            @NotBlank(message = "Task ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id,
            @Valid @RequestBody TaskRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.update(id, dto));
    }

    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<?> assignUserById(
            @PathVariable
            @NotBlank(message = "Task ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String taskId,
            @Valid @RequestBody AssignUserRequestDto dto
    ) {
        taskService.assignUserById(taskId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePersonalTask(
            @PathVariable
            @NotBlank(message = "Task ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id
    ) {
        taskService.deletePersonalTask(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("{taskId}/team/{teamId}")
    public ResponseEntity<?> deleteTeamTask(
            @PathVariable
            @NotBlank(message = "Team ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String teamId,
            @PathVariable
            @NotBlank(message = "Task ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String taskId
    ) {
        taskService.deleteTeamTask(teamId, taskId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
