package com.example.task_manager.mappers;

import com.example.task_manager.dtos.request.team.TeamRequestDto;
import com.example.task_manager.dtos.response.team.TeamResponseDto;
import com.example.task_manager.entities.Team;
import com.example.task_manager.utils.TeamCollectionData;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {
    public Team toEntity(TeamRequestDto dto) {
        return Team.builder()
                .id(dto.getId())
                .name(dto.getName())
                .ownerId(dto.getOwnerId())
                .build();
    }

    public TeamResponseDto toDto(Team team, TeamCollectionData teamData) {
        return TeamResponseDto.builder()
                .id(team.getId())
                .name(team.getName())
                .ownerId(team.getOwnerId())
                .members(teamData.getMembers())
                .tasks(teamData.getTasks())
                .build();
    }
}
