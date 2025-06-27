package com.example.task_manager.dtos.request.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignUserRequestDto {
    @NotBlank(message = "User ID is required")
    @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
    private String userId;
}
