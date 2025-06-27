package com.example.task_manager.dtos.request.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequestDto {
    private String id;

    @NotBlank(message = "Team name is required")
    @Size(max = 100, message = "Team name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Owner ID is required")
    @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
    private String ownerId;
}
