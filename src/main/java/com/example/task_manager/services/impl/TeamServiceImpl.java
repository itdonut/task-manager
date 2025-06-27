package com.example.task_manager.services.impl;

import com.example.task_manager.dtos.request.task.AssignUserRequestDto;
import com.example.task_manager.dtos.request.team.TeamRequestDto;
import com.example.task_manager.dtos.response.task.TaskResponseDto;
import com.example.task_manager.dtos.response.team.TeamResponseDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;
import com.example.task_manager.entities.Team;
import com.example.task_manager.exceptions.ResourceNotFoundException;
import com.example.task_manager.exceptions.UserIsNotTeamMemberException;
import com.example.task_manager.mappers.TeamMapper;
import com.example.task_manager.repositories.TeamRepository;
import com.example.task_manager.services.ITaskService;
import com.example.task_manager.services.ITeamService;
import com.example.task_manager.services.IUserService;
import com.example.task_manager.utils.DateTimeUTC;
import com.example.task_manager.utils.TeamCollectionData;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements ITeamService {
    private final TeamRepository teamRepository;
    private final IUserService userService;
    private final ITaskService taskService;
    private final TeamMapper teamMapper;

    public TeamServiceImpl(TeamRepository teamRepository, IUserService userService, @Lazy ITaskService taskService, TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.taskService = taskService;
        this.teamMapper = teamMapper;
    }

    @Override
    public List<TeamResponseDto> getTeamsByUserId(String id) {
        UserResponseDto user = userService.getUserById(id);

        List<Team> teams = teamRepository.findByOwnerId(id);
        return teams.stream()
                .map(team -> teamMapper.toDto(team, this.collectTeamData(team)))
                .collect(Collectors.toList());
    }

    @Override
    public TeamResponseDto create(TeamRequestDto dto) {
        UserResponseDto owner = userService.getUserById(dto.getOwnerId());
        Team team = teamMapper.toEntity(dto);
        team.setMembersId(List.of(dto.getOwnerId()));
        team.setCreatedAt(DateTimeUTC.now());
        team.setModifiedAt(DateTimeUTC.now());
        return teamMapper.toDto(
                teamRepository.save(team),
                TeamCollectionData.builder()
                        .members(List.of(owner))
                        .build()
        );
    }

    @Override
    public TeamResponseDto update(String id, TeamRequestDto dto) {
        Team team = getTeamById(id);

        Team updatedTeam = teamMapper.toEntity(dto);
        updatedTeam.setId(team.getId());
        updatedTeam.setCreatedAt(team.getCreatedAt());
        updatedTeam.setModifiedAt(DateTimeUTC.now());
        updatedTeam.setMembersId(team.getMembersId());

        return teamMapper.toDto(teamRepository.save(updatedTeam), this.collectTeamData(updatedTeam));
    }

    @Override
    public void updateModifiedAtById(String id) {
        Team team = this.getTeamById(id);
        team.setModifiedAt(DateTimeUTC.now());
        teamRepository.save(team);
    }

    @Override
    public void addMember(String teamId, AssignUserRequestDto dto) {
        assertTeamExistsById(teamId);
        Team team = this.getTeamById(teamId);

        String userId = dto.getUserId();
        userService.assertUserExistsById(userId);

        if (!team.getMembersId().contains(userId)) { // may throw ResourceAlreadyExistsException
            team.getMembersId().add(userId);
            team.setModifiedAt(DateTimeUTC.now());
        }

        teamRepository.save(team);
    }

    @Override
    public void delete(String id) {
        assertTeamExistsById(id);
        taskService.deleteTeamTasksByTeamId(id);
        teamRepository.deleteById(id);
    }

    @Override
    public void deleteUserFromAllTeamsById(String userId) {
        userService.assertUserExistsById(userId);

        teamRepository.findByMembersId(userId)
                .stream()
                .peek(team -> {
                    List<String> updatedMembership = team.getMembersId()
                            .stream()
                            .filter(id -> !Objects.equals(id, userId))
                            .toList();

                    team.setMembersId(updatedMembership);
                    team.setModifiedAt(DateTimeUTC.now());
                })
                .forEach(teamRepository::save);
    }

    @Override
    public void assertTeamExistsById(String id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Team with id=" + id + " doesn't exist",
                    DateTimeUTC.now()
            );
        }
    }

    @Override
    public void assertUserIsTeamMember(String teamId, String userId) {
        Team team = this.getTeamById(teamId);
        if (!team.getMembersId().contains(userId)) {
            throw new UserIsNotTeamMemberException(
                    "User with id=" + userId + " isn't a member of the team with id=" + teamId,
                    DateTimeUTC.now()
            );
        }
    }

    private Team getTeamById(String id) {
        return teamRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Team with id=" + id + " doesn't exist",
                        DateTimeUTC.now()
                )
        );
    }

    private TeamCollectionData collectTeamData(Team team) {
        List<UserResponseDto> members = team.getMembersId().stream()
                .filter(userService::isUserPresentById)
                .map(userService::getUserById)
                .collect(Collectors.toList());

        List<TaskResponseDto> tasks = taskService.getTasksByTeamId(team.getId());

        return TeamCollectionData.builder()
                .members(members)
                .tasks(tasks)
                .build();
    }
}
