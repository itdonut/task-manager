package com.example.task_manager.services.impl;

import com.example.task_manager.dtos.request.team.TeamRequestDto;
import com.example.task_manager.dtos.response.team.TeamResponseDto;
import com.example.task_manager.repositories.TeamRepository;
import com.example.task_manager.services.ITeamService;

public class TeamServiceImpl implements ITeamService {
    private final TeamRepository teamRepository;

    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public TeamResponseDto create(TeamRequestDto dto) {
        return null;
    }

    @Override
    public TeamResponseDto update(TeamRequestDto dto) {
        return null;
    }

    @Override
    public void delete(String id) {

    }
}
