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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("[TeamService] Fetching teams for user with ID: {}", id);
        UserResponseDto user = userService.getUserById(id);

        List<Team> teams = teamRepository.findByOwnerId(id);
        log.info("[TeamService] Found {} teams for user with ID: {}", teams.size(), id);
        return teams.stream()
                .map(team -> teamMapper.toDto(team, this.collectTeamData(team)))
                .collect(Collectors.toList());
    }

    @Override
    public TeamResponseDto create(TeamRequestDto dto) {
        log.info("[TeamService] Creating team for owner with ID: {}", dto.getOwnerId());
        UserResponseDto owner = userService.getUserById(dto.getOwnerId());

        Team team = teamMapper.toEntity(dto);
        team.setMembersId(List.of(dto.getOwnerId()));
        team.setCreatedAt(DateTimeUTC.now());
        team.setModifiedAt(DateTimeUTC.now());

        Team savedTeam = teamRepository.save(team);
        log.info("[TeamService] Successfully created team with ID: {}", savedTeam.getId());
        return teamMapper.toDto(savedTeam, TeamCollectionData.builder()
                .members(List.of(owner))
                .build()
        );
    }

    @Override
    public TeamResponseDto update(String id, TeamRequestDto dto) {
        log.info("[TeamService] Updating team with ID: {}", id);
        Team team = getTeamById(id);

        Team updatedTeam = teamMapper.toEntity(dto);
        updatedTeam.setId(team.getId());
        updatedTeam.setCreatedAt(team.getCreatedAt());
        updatedTeam.setModifiedAt(DateTimeUTC.now());
        updatedTeam.setMembersId(team.getMembersId());

        Team savedTeam = teamRepository.save(updatedTeam);
        log.info("[TeamService] Successfully updated team with ID: {}", id);
        return teamMapper.toDto(savedTeam, this.collectTeamData(updatedTeam));
    }

    @Override
    public void updateModifiedAtById(String id) {
        log.info("[TeamService] Updating modifiedAt timestamp for team with ID: {}", id);
        Team team = this.getTeamById(id);
        team.setModifiedAt(DateTimeUTC.now());
        teamRepository.save(team);
        log.info("[TeamService] Updated modifiedAt for team with ID: {}", id);
    }

    @Override
    public void addMember(String teamId, AssignUserRequestDto dto) {
        String userId = dto.getUserId();
        log.info("[TeamService] Adding user with ID: {} to team with ID: {}", userId, teamId);
        assertTeamExistsById(teamId);
        Team team = this.getTeamById(teamId);

        userService.assertUserExistsById(userId);
        if (!team.getMembersId().contains(userId)) { // may throw ResourceAlreadyExistsException
            team.getMembersId().add(userId);
            team.setModifiedAt(DateTimeUTC.now());
            log.info("[TeamService] User with ID: {} added to team with ID: {}", userId, teamId);
        } else {
            log.warn("[TeamService] User with ID: {} already in team with ID: {}", userId, teamId);
        }

        teamRepository.save(team);
    }

    @Override
    public void delete(String id) {
        log.info("[TeamService] Deleting team with ID: {}", id);
        assertTeamExistsById(id);
        taskService.deleteTeamTasksByTeamId(id);
        teamRepository.deleteById(id);
        log.info("[TeamService] Successfully deleted team with ID: {}", id);
    }

    @Override
    public void deleteUserFromAllTeamsById(String userId) {
        log.info("[TeamService] Removing user with ID: {} from all teams", userId);
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
                .forEach(team -> {
                    teamRepository.save(team);
                    log.info("[TeamService] User with ID: {} removed from team with ID: {}", userId, team.getId());
                });
    }

    @Override
    public void assertTeamExistsById(String id) {
        if (!teamRepository.existsById(id)) {
            log.warn("[TeamService] Team with ID: {} does not exist", id);
            throw new ResourceNotFoundException(
                    "Team with ID: " + id + " doesn't exist",
                    DateTimeUTC.now()
            );
        }
    }

    @Override
    public void assertUserIsTeamMember(String teamId, String userId) {
        Team team = this.getTeamById(teamId);
        if (!team.getMembersId().contains(userId)) {
            log.warn("[TeamService] User with ID: {} is not a member of team with ID: {}", userId, teamId);
            throw new UserIsNotTeamMemberException(
                    "User with ID: " + userId + " isn't a member of the team with ID: " + teamId,
                    DateTimeUTC.now()
            );
        }
    }

    private Team getTeamById(String id) {
        return teamRepository.findById(id).orElseThrow(() -> {
            log.warn("[TeamService] Team with ID: {} not found", id);
            return new ResourceNotFoundException(
                    "Team with ID: " + id + " doesn't exist",
                    DateTimeUTC.now()
            );
        });
    }

    private TeamCollectionData collectTeamData(Team team) {
        List<UserResponseDto> members = team.getMembersId().stream()
                .filter(userService::isUserPresentById)
                .map(userService::getUserById)
                .collect(Collectors.toList());

        List<TaskResponseDto> tasks = taskService.getTasksByTeamId(team.getId());
        log.info("[TeamService] Found {} members and {} tasks for team with ID: {}",
                members.size(), tasks.size(), team.getId());
        return TeamCollectionData.builder()
                .members(members)
                .tasks(tasks)
                .build();
    }
}
