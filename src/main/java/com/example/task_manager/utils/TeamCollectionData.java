package com.example.task_manager.utils;

import com.example.task_manager.dtos.response.task.TaskResponseDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCollectionData {
    private List<UserResponseDto> members;
    private List<TaskResponseDto> tasks;
}
