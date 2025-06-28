package com.example.task_manager.controllers;

import com.example.task_manager.dtos.request.task.AssignUserRequestDto;
import com.example.task_manager.dtos.request.team.TeamRequestDto;
import com.example.task_manager.dtos.response.team.TeamResponseDto;
import com.example.task_manager.services.ITeamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/teams")
@Validated
public class TeamController {
    private final ITeamService teamService;
    private final HttpServletRequest request;

    public TeamController(ITeamService teamService, HttpServletRequest request) {
        this.teamService = teamService;
        this.request = request;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<TeamResponseDto>> getTeamsByUserId(
            @PathVariable
            @NotBlank(message = "User ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id
    ) {
        log.info("[TeamController][{} {}] START get teams for user with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        List<TeamResponseDto> teams = teamService.getTeamsByUserId(id);
        log.info("[TeamController][{} {}] SUCCESS got {} teams for user with ID: {}",
                request.getMethod(), request.getRequestURI(), teams.size(), id);
        return ResponseEntity.status(HttpStatus.OK).body(teams);
    }

    @PostMapping
    public ResponseEntity<TeamResponseDto> createTeam(@Valid @RequestBody TeamRequestDto dto) {
        log.info("[TeamController][{} {}] START create new team with name: {}",
                request.getMethod(), request.getRequestURI(), dto.getName());
        TeamResponseDto created = teamService.create(dto);
        log.info("[TeamController][{} {}] SUCCESS created team with ID: {}",
                request.getMethod(), request.getRequestURI(), created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponseDto> updateTeam(
            @PathVariable
            @NotBlank(message = "Team ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id,
            @Valid @RequestBody TeamRequestDto dto
    ) {
        log.info("[TeamController][{} {}] START update team with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        TeamResponseDto updated = teamService.update(id, dto);
        log.info("[TeamController][{} {}] SUCCESS updated team with ID: {}",
                request.getMethod(), request.getRequestURI(), updated.getId());
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/{teamId}/add-member")
    public ResponseEntity<?> addMemberToTeam(
            @PathVariable
            @NotBlank(message = "Team ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String teamId,
            @Valid @RequestBody AssignUserRequestDto dto
    ) {
        log.info("[TeamController][{} {}] START add user with ID: {} to team with ID: {}",
                request.getMethod(), request.getRequestURI(), dto.getUserId(), teamId);
        teamService.addMember(teamId, dto);
        log.info("[TeamController][{} {}] SUCCESS added user with ID: {} to team with ID: {}",
                request.getMethod(), request.getRequestURI(), dto.getUserId(), teamId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(
            @PathVariable
            @NotBlank(message = "Team ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id
    ) {
        log.info("[TeamController][{} {}] START delete team with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        teamService.delete(id);
        log.info("[TeamController][{} {}] SUCCESS deleted Team with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
