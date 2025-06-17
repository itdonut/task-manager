package com.example.task_manager.dtos.response.task;

import com.example.task_manager.enums.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private String id;
    private String title;
    private String description;
    private TaskPriority priority;
    private String userId;
    private String teamId;
    private Date start;
    private Date end;
}
