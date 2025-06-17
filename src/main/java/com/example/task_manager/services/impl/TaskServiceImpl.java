package com.example.task_manager.services.impl;

import com.example.task_manager.dtos.request.task.TaskRequestDto;
import com.example.task_manager.dtos.response.task.TaskResponseDto;
import com.example.task_manager.services.ITaskService;

import java.util.List;

public class TaskServiceImpl implements ITaskService {
    @Override
    public List<TaskResponseDto> getTasksByUserId(String id) {
        return List.of();
    }

    @Override
    public List<TaskResponseDto> getTasksByTeamId(String id) {
        return List.of();
    }

    @Override
    public TaskResponseDto create(TaskRequestDto dto) {
        return null;
    }

    @Override
    public TaskResponseDto update(String id, TaskRequestDto dto) {
        return null;
    }

    @Override
    public void delete(String id) {

    }
}
