package com.example.task_manager.controllers;

import com.example.task_manager.dtos.request.task.AssignUserRequestDto;
import com.example.task_manager.dtos.request.task.TaskRequestDto;
import com.example.task_manager.dtos.response.task.TaskResponseDto;
import com.example.task_manager.services.ITaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@Validated
public class TaskController {
    private final ITaskService taskService;
    private final HttpServletRequest request;

    public TaskController(ITaskService taskService, HttpServletRequest request) {
        this.taskService = taskService;
        this.request = request;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<TaskResponseDto>> getTasksByUserId(
            @PathVariable
            @NotBlank(message = "User ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id
    ) {
        log.info("[TaskController][{} {}] START get tasks for user with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        List<TaskResponseDto> tasks = taskService.getTasksByUserId(id);
        log.info("[TaskController][{} {}] SUCCESS found {} tasks for user with ID: {}",
                request.getMethod(), request.getRequestURI(), tasks.size(), id);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PostMapping("/user/{id}")
    public ResponseEntity<TaskResponseDto> createTaskForUser(
            @PathVariable
            @NotBlank(message = "User ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id,
            @Valid @RequestBody TaskRequestDto dto
    ) {
        log.info("[TaskController][{} {}] START create personal task for user with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        TaskResponseDto createdTask = taskService.createUserTask(id, dto);
        log.info("[TaskController][{} {}] SUCCESS created personal task with ID: {} for user with ID: {}",
                request.getMethod(), request.getRequestURI(), createdTask.getId(), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PostMapping("/team/{id}")
    public ResponseEntity<TaskResponseDto> createTaskForTeam(
            @PathVariable
            @NotBlank(message = "Team ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id,
            @Valid @RequestBody TaskRequestDto dto
    ) {
        log.info("[TaskController][{} {}] START create task for team with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        TaskResponseDto createdTask = taskService.createTeamTask(id, dto);
        log.info("[TaskController][{} {}] SUCCESS created task with ID: {} for team with ID: {}",
                request.getMethod(), request.getRequestURI(), createdTask.getId(), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable
            @NotBlank(message = "Task ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id,
            @Valid @RequestBody TaskRequestDto dto
    ) {
        log.info("[TaskController][{} {}] START update task with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        TaskResponseDto updatedTask = taskService.update(id, dto);
        log.info("[TaskController][{} {}] SUCCESS updated task with ID: {}",
                request.getMethod(), request.getRequestURI(), updatedTask.getId());
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<?> assignUserById(
            @PathVariable
            @NotBlank(message = "Task ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String taskId,
            @Valid @RequestBody AssignUserRequestDto dto
    ) {
        log.info("[TaskController][{} {}] START assign user with ID: {} to task with ID: {}",
                request.getMethod(), request.getRequestURI(), dto.getUserId(), taskId);
        taskService.assignUserById(taskId, dto);
        log.info("[TaskController][{} {}] SUCCESS assigned user with ID: {} to task with ID: {}",
                request.getMethod(), request.getRequestURI(), dto.getUserId(), taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePersonalTask(
            @PathVariable
            @NotBlank(message = "Task ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id
    ) {
        log.info("[TaskController][{} {}] START delete personal task with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        taskService.deletePersonalTask(id);
        log.info("[TaskController][{} {}] SUCCESS deleted personal task with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
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
        log.info("[TaskController][{} {}] START delete team task with ID: {} from team with ID: {}",
                request.getMethod(), request.getRequestURI(), taskId, teamId);
        taskService.deleteTeamTask(teamId, taskId);
        log.info("[TaskController][{} {}] SUCCESS deleted team task with ID: {} from team with ID: {}",
                request.getMethod(), request.getRequestURI(), taskId, teamId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
