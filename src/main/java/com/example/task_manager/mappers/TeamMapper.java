package com.example.task_manager.mappers;

import com.example.task_manager.dtos.request.team.TeamRequestDto;
import com.example.task_manager.dtos.response.team.TeamResponseDto;
import com.example.task_manager.entities.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {
    public Team toEntity(TeamRequestDto dto) {
        return Team.builder()
                .name(dto.getName())
                .ownerId(dto.getOwnerId())
                .build();
    }

    public TeamResponseDto toDto(Team team) {
        return TeamResponseDto.builder()
                .name(team.getName())
    }
}
