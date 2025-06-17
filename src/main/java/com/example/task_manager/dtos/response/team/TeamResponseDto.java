package com.example.task_manager.dtos.response.team;

import com.example.task_manager.entities.User;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponseDto {
    private String name;
    private User owner;
}
