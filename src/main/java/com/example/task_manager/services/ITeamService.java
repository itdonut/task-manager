package com.example.task_manager.services;

import com.example.task_manager.dtos.request.team.TeamRequestDto;
import com.example.task_manager.dtos.response.team.TeamResponseDto;

import java.util.List;

public interface ITeamService {
    public TeamResponseDto create(TeamRequestDto dto);
    public TeamResponseDto update(TeamRequestDto dto);
    public void delete(String id);
}
