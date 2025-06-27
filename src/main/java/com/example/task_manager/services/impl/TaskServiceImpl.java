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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        userService.assertUserExistsById(id);
        return taskRepository.findByAssignedUsersIdAndType(id, TaskType.PERSONAL)
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponseDto> getTasksByTeamId(String id) {
        teamService.assertTeamExistsById(id);
        return taskRepository.findByTeamIdAndType(id, TaskType.TEAM)
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDto createUserTask(String id, TaskRequestDto dto) {
        userService.assertUserExistsById(id);

        Task task = taskMapper.toEntity(dto);
        task.setType(TaskType.PERSONAL);
        task.setStatus(TaskStatus.NEW);
        task.setCreatedAt(DateTimeUTC.now());
        task.setModifiedAt(DateTimeUTC.now());
        task.setAssignedUsersId(List.of(id));
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public TaskResponseDto createTeamTask(String id, TaskRequestDto dto) {
        teamService.assertTeamExistsById(id);

        Task task = taskMapper.toEntity(dto);
        task.setType(TaskType.TEAM);
        task.setStatus(TaskStatus.NEW);
        task.setCreatedAt(DateTimeUTC.now());
        task.setModifiedAt(DateTimeUTC.now());
        task.setAssignedUsersId(new ArrayList<>());
        task.setTeamId(id);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public TaskResponseDto update(String id, TaskRequestDto dto) {
        Task task = getTaskById(id);

        Task updatedTask = taskMapper.toEntity(dto);
        updatedTask.setId(task.getId());
        updatedTask.setCreatedAt(task.getCreatedAt());
        updatedTask.setModifiedAt(DateTimeUTC.now());
        updatedTask.setType(task.getType()); // because cannot be modified
        updatedTask.setAssignedUsersId(task.getAssignedUsersId());
        return taskMapper.toDto(taskRepository.save(updatedTask));
    }

    @Override
    public void assignUserById(String taskId, AssignUserRequestDto dto) {
        String userId = dto.getUserId();
        userService.assertUserExistsById(userId);

        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Task with id=" + taskId + " doesn't exist",
                        DateTimeUTC.now()
                )
        );
        teamService.assertUserIsTeamMember(task.getTeamId(), userId);

        if (!task.getAssignedUsersId().contains(userId))
            task.getAssignedUsersId().add(userId);

        taskRepository.save(task);
    }

    @Override
    public void unassignUserById(String userId) {
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
                .forEach(taskRepository::save);
    }

    @Override
    public void deletePersonalTask(String id) {
        this.assertPersonalTaskExistsById(id);
        taskRepository.deleteById(id);
    }

    @Override
    public void deleteTeamTask(String teamId, String taskId) {
        assertTeamTaskExistsById(taskId);
        teamService.assertTeamExistsById(teamId);
        teamService.updateModifiedAtById(teamId);
        taskRepository.deleteByIdAndTeamIdAndType(taskId, teamId, TaskType.TEAM);
    }

    @Override
    public void deleteTeamTasksByTeamId(String teamId) {
        taskRepository.deleteByTeamId(teamId);
    }

    @Override
    public void deleteUserTasksByUserId(String id) {
        taskRepository.deleteByAssignedUsersIdAndType(id, TaskType.PERSONAL);
    }

    @Override
    public void assertPersonalTaskExistsById(String id) {
        if(!taskRepository.existsByIdAndType(id, TaskType.PERSONAL)) {
            throw new ResourceNotFoundException(
                    "Task with id=" + id + " doesn't exist",
                    DateTimeUTC.now()
            );
        }
    }

    @Override
    public void assertTeamTaskExistsById(String id) {
        if(!taskRepository.existsByIdAndType(id, TaskType.TEAM)) {
            throw new ResourceNotFoundException(
                    "Task with id=" + id + " doesn't exist",
                    DateTimeUTC.now()
            );
        }
    }

    private Task getTaskById(String id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Task with id=" + id + " doesn't exist",
                        DateTimeUTC.now()
                )
        );
    }
}
