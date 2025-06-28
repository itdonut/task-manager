package com.example.task_manager.controllers;

import com.example.task_manager.dtos.request.user.UpdateUserPasswordDto;
import com.example.task_manager.dtos.request.user.UserRequestDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;
import com.example.task_manager.services.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    private final IUserService userService;
    private final HttpServletRequest request;

    public UserController(IUserService userService, HttpServletRequest request) {
        this.userService = userService;
        this.request = request;
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable
            @NotBlank(message = "User ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id,
            @Valid @RequestBody UserRequestDto dto
    ) {
        log.info("[UserController][{} {}] START update user with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        UserResponseDto updatedUser = userService.update(id, dto);
        log.info("[UserController][{} {}] SUCCESS updated user with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<String> updateUserPassword(
            @PathVariable
            @NotBlank(message = "User ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id,
            @Valid @RequestBody UpdateUserPasswordDto dto
    ) {
        log.info("[UserController][{} {}] START update password for user with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        userService.updatePassword(id, dto);
        log.info("[UserController][{} {}] SUCCESS updated password for user with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable
            @NotBlank(message = "User ID is required")
            @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Invalid MongoDB ID format")
            String id
    ) {
        log.info("[UserController][{} {}] START delete user with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        userService.delete(id);
        log.info("[UserController][{} {}] SUCCESS deleted user with ID: {}",
                request.getMethod(), request.getRequestURI(), id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
