package com.example.task_manager.services;

import com.example.task_manager.dtos.request.task.AssignUserRequestDto;
import com.example.task_manager.dtos.request.task.TaskRequestDto;
import com.example.task_manager.dtos.response.task.TaskResponseDto;

import java.util.List;

public interface ITaskService {
    public List<TaskResponseDto> getTasksByUserId(String id);
    public List<TaskResponseDto> getTasksByTeamId(String id);
    public TaskResponseDto createUserTask(String id, TaskRequestDto dto);
    public TaskResponseDto createTeamTask(String id, TaskRequestDto dto);
    public TaskResponseDto update(String id, TaskRequestDto dto);
    public void assignUserById(String taskId, AssignUserRequestDto dto);
    public void unassignUserById(String userId);
    public void deletePersonalTask(String id);
    public void deleteTeamTask(String teamId, String taskId);
    public void deleteTeamTasksByTeamId(String teamId);
    public void deleteUserTasksByUserId(String id);
    public void assertPersonalTaskExistsById(String id);
    public void assertTeamTaskExistsById(String id);
}
