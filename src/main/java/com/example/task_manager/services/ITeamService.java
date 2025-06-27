package com.example.task_manager.services;

import com.example.task_manager.dtos.request.task.AssignUserRequestDto;
import com.example.task_manager.dtos.request.team.TeamRequestDto;
import com.example.task_manager.dtos.response.team.TeamResponseDto;

import java.util.List;

public interface ITeamService {
    public List<TeamResponseDto> getTeamsByUserId(String id);
    public TeamResponseDto create(TeamRequestDto dto);
    public TeamResponseDto update(String id, TeamRequestDto dto);
    public void updateModifiedAtById(String id);
    public void addMember(String teamId, AssignUserRequestDto dto);
    public void delete(String id);
    public void deleteUserFromAllTeamsById(String userId);
    public void assertTeamExistsById(String id);
    public void assertUserIsTeamMember(String teamId, String userId);
}
