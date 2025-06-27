package com.example.task_manager.mappers;

import com.example.task_manager.dtos.request.task.TaskRequestDto;
import com.example.task_manager.dtos.response.task.TaskResponseDto;
import com.example.task_manager.entities.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public Task toEntity(TaskRequestDto dto) {
        return Task.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .status(dto.getStatus())
                .start(dto.getStart())
                .end(dto.getEnd())
                .build();
    }

    public TaskResponseDto toDto(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .assignedUsersId(task.getAssignedUsersId())
                .start(task.getStart())
                .end(task.getEnd())
                .build();
    }
}
