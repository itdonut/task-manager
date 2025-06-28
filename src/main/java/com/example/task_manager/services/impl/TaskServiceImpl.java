package com.example.task_manager.services.impl;

import com.example.task_manager.dtos.request.task.AssignUserRequestDto;
import com.example.task_manager.dtos.request.task.TaskRequestDto;
import com.example.task_manager.dtos.response.task.TaskResponseDto;
import com.example.task_manager.entities.Task;
import com.example.task_manager.enums.TaskStatus;
import com.example.task_manager.enums.TaskType;
import com.example.task_manager.exceptions.ResourceNotFoundException;
import com.example.task_manager.mappers.TaskMapper;
import com.example.task_manager.repositories.TaskRepository;
import com.example.task_manager.services.ITaskService;
import com.example.task_manager.services.ITeamService;
import com.example.task_manager.services.IUserService;
import com.example.task_manager.utils.DateTimeUTC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskServiceImpl implements ITaskService {
    private final TaskRepository taskRepository;
    private final IUserService userService;
    private final ITeamService teamService;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, IUserService userService, ITeamService teamService, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.teamService = teamService;
        this.taskMapper = taskMapper;
    }

    @Override
    public List<TaskResponseDto> getTasksByUserId(String id) {
        log.info("[TaskService] Fetching personal tasks for user with ID: {}", id);
        userService.assertUserExistsById(id);
        List<TaskResponseDto> tasks = taskRepository.findByAssignedUsersIdAndType(id, TaskType.PERSONAL)
                .stream()
                .map(taskMapper::toDto)
                .toList();
        log.info("[TaskService] Found {} personal tasks for user with ID: {}", tasks.size(), id);
        return tasks;
    }

    @Override
    public List<TaskResponseDto> getTasksByTeamId(String id) {
        log.info("[TaskService] Fetching team tasks for team with ID: {}", id);
        teamService.assertTeamExistsById(id);
        List<TaskResponseDto> tasks = taskRepository.findByTeamIdAndType(id, TaskType.TEAM)
                .stream()
                .map(taskMapper::toDto)
                .toList();
        log.info("[TaskService] Found {} team tasks for team with ID: {}", tasks.size(), id);
        return tasks;
    }

    @Override
    public TaskResponseDto createUserTask(String id, TaskRequestDto dto) {
        log.info("[TaskService] Creating personal task for user with ID: {}", id);
        userService.assertUserExistsById(id);

        Task task = taskMapper.toEntity(dto);
        task.setType(TaskType.PERSONAL);
        task.setStatus(TaskStatus.NEW);
        task.setCreatedAt(DateTimeUTC.now());
        task.setModifiedAt(DateTimeUTC.now());
        task.setAssignedUsersId(List.of(id));

        Task saved = taskRepository.save(task);
        log.info("[TaskService] Created personal task with ID: {} for user with ID: {}", saved.getId(), id);
        return taskMapper.toDto(saved);
    }

    @Override
    public TaskResponseDto createTeamTask(String id, TaskRequestDto dto) {
        log.info("[TaskService] Creating team task for team with ID: {}", id);
        teamService.assertTeamExistsById(id);

        Task task = taskMapper.toEntity(dto);
        task.setType(TaskType.TEAM);
        task.setStatus(TaskStatus.NEW);
        task.setCreatedAt(DateTimeUTC.now());
        task.setModifiedAt(DateTimeUTC.now());
        task.setAssignedUsersId(new ArrayList<>());
        task.setTeamId(id);

        Task saved = taskRepository.save(task);
        log.info("[TaskService] Created team task with ID: {} for team with ID: {}", saved.getId(), id);
        return taskMapper.toDto(saved);
    }

    @Override
    public TaskResponseDto update(String id, TaskRequestDto dto) {
        log.info("[TaskService] Updating task with ID: {}", id);
        Task task = getTaskById(id);

        Task updatedTask = taskMapper.toEntity(dto);
        updatedTask.setId(task.getId());
        updatedTask.setCreatedAt(task.getCreatedAt());
        updatedTask.setModifiedAt(DateTimeUTC.now());
        updatedTask.setType(task.getType()); // because cannot be modified
        updatedTask.setAssignedUsersId(task.getAssignedUsersId());

        Task saved = taskRepository.save(updatedTask);
        log.info("[TaskService] Task with ID: {} updated successfully", id);
        return taskMapper.toDto(saved);
    }

    @Override
    public void assignUserById(String taskId, AssignUserRequestDto dto) {
        String userId = dto.getUserId();
        log.info("[TaskService] Assigning user with ID: {} to task with ID: {}", userId, taskId);
        userService.assertUserExistsById(userId);

        Task task = this.getTaskById(taskId);
        teamService.assertUserIsTeamMember(task.getTeamId(), userId);

        if (!task.getAssignedUsersId().contains(userId))
            task.getAssignedUsersId().add(userId);

        taskRepository.save(task);
        log.info("[TaskService] User with ID: {} assigned to task with ID: {}", userId, taskId);
    }

    @Override
    public void unassignUserById(String userId) {
        log.info("[TaskService] Unassigning user with ID: {} from all TEAM tasks", userId);
        taskRepository.findByAssignedUsersIdAndType(userId, TaskType.TEAM)
                .stream()
                .peek(task -> {
                    List<String> updatedUsers = task.getAssignedUsersId()
                            .stream()
                            .filter(id -> !Objects.equals(id, userId))
                            .collect(Collectors.toList());
                    task.setAssignedUsersId(updatedUsers);
                    task.setModifiedAt(DateTimeUTC.now());
                })
                .forEach(task -> {
                    taskRepository.save(task);
                    log.info("[TaskService] User with ID: {} unassigned from task with ID: {}", userId, task.getId());
                });
    }

    @Override
    public void deletePersonalTask(String id) {
        log.info("[TaskService] Deleting personal task with ID: {}", id);
        this.assertPersonalTaskExistsById(id);
        taskRepository.deleteById(id);
        log.info("[TaskService] Personal task with ID: {} deleted", id);
    }

    @Override
    public void deleteTeamTask(String teamId, String taskId) {
        log.info("[TaskService] Deleting team task with ID: {} from team with ID: {}", taskId, teamId);
        assertTeamTaskExistsById(taskId);
        teamService.assertTeamExistsById(teamId);
        teamService.updateModifiedAtById(teamId);
        taskRepository.deleteByIdAndTeamIdAndType(taskId, teamId, TaskType.TEAM);
        log.info("[TaskService] Team task with ID: {} deleted", taskId);
    }

    @Override
    public void deleteTeamTasksByTeamId(String teamId) {
        log.info("[TaskService] Deleting all tasks for team with ID: {}", teamId);
        taskRepository.deleteByTeamId(teamId);
        log.info("[TaskService] All tasks for team with ID: {} deleted", teamId);
    }

    @Override
    public void deleteUserTasksByUserId(String id) {
        log.info("[TaskService] Deleting personal tasks for user with ID: {}", id);
        taskRepository.deleteByAssignedUsersIdAndType(id, TaskType.PERSONAL);
        log.info("[TaskService] Personal tasks for user with ID: {} deleted", id);
    }

    @Override
    public void assertPersonalTaskExistsById(String id) {
        if(!taskRepository.existsByIdAndType(id, TaskType.PERSONAL)) {
            log.warn("[TaskService] Personal task with ID: {} does not exist", id);
            throw new ResourceNotFoundException("Task with ID: " + id + " doesn't exist", DateTimeUTC.now());
        }
    }

    @Override
    public void assertTeamTaskExistsById(String id) {
        if(!taskRepository.existsByIdAndType(id, TaskType.TEAM)) {
            log.warn("[TaskService] Team task with ID: {} does not exist", id);
            throw new ResourceNotFoundException("Task with ID: " + id + " doesn't exist", DateTimeUTC.now());
        }
    }

    private Task getTaskById(String id) {
        return taskRepository.findById(id).orElseThrow(() -> {
            log.warn("[TaskService] Task with ID: {} not found", id);
            return new ResourceNotFoundException("Task with ID: " + id + " doesn't exist", DateTimeUTC.now());
        });
    }
}
