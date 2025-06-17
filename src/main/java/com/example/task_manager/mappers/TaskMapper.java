package com.example.task_manager.mappers;

import com.example.task_manager.dtos.response.task.TaskResponseDto;
import com.example.task_manager.entities.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskResponseDto toDto(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .
    }
}
