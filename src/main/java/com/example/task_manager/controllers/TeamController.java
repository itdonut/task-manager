package com.example.task_manager.controllers;

import com.example.task_manager.dtos.request.task.AssignUserRequestDto;
import com.example.task_manager.dtos.request.team.TeamRequestDto;
import com.example.task_manager.dtos.response.team.TeamResponseDto;
import com.example.task_manager.services.ITeamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@Validated
public class TeamController {
    private final ITeamService teamService;

    public TeamController(ITeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<TeamResponseDto>> getTeamsByUserId(
            @PathVariable
            @NotBlank(message = "User ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getTeamsByUserId(id));
    }

    @PostMapping
    public ResponseEntity<TeamResponseDto> createTeam(@Valid @RequestBody TeamRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponseDto> updateTeam(
            @PathVariable
            @NotBlank(message = "Team ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id,
            @Valid @RequestBody TeamRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.update(id, dto));
    }

    @PatchMapping("/{teamId}/add-member")
    public ResponseEntity<?> addMemberToTeam(
            @PathVariable
            @NotBlank(message = "Team ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String teamId,
            @Valid @RequestBody AssignUserRequestDto dto
    ) {
        teamService.addMember(teamId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(
            @PathVariable
            @NotBlank(message = "Team ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id
    ) {
        teamService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
