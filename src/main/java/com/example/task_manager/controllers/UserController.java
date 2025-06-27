package com.example.task_manager.controllers;

import com.example.task_manager.dtos.request.user.UpdateUserPasswordDto;
import com.example.task_manager.dtos.request.user.UserRequestDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;
import com.example.task_manager.services.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable
            @NotBlank(message = "User ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id,
            @Valid @RequestBody UserRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(id, dto));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<String> updateUserPassword(
            @PathVariable
            @NotBlank(message = "User ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id,
            @Valid @RequestBody UpdateUserPasswordDto dto
    ) {
        userService.updatePassword(id, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable
            @NotBlank(message = "User ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id
    ) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
