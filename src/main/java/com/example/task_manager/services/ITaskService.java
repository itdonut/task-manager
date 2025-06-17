package com.example.task_manager.services;

import com.example.task_manager.dtos.request.task.TaskRequestDto;
import com.example.task_manager.dtos.response.task.TaskResponseDto;

import java.util.List;

public interface ITaskService {
    public List<TaskResponseDto> getTasksByUserId(String id);
    public List<TaskResponseDto> getTasksByTeamId(String id);
    public TaskResponseDto create(TaskRequestDto dto);
    public TaskResponseDto update(String id, TaskRequestDto dto);
    public void delete(String id);
}
