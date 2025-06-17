package com.example.task_manager.controllers;

import com.example.task_manager.dtos.request.user.UpdateUserPasswordRequestDto;
import com.example.task_manager.dtos.request.user.UserRequestDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;
import com.example.task_manager.services.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable String id,
            @RequestBody UserRequestDto userRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(id, userRequestDto));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<String> updateUserPassword(
            @PathVariable String id,
            @RequestBody UpdateUserPasswordRequestDto dto
    ) {
        userService.updatePassword(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body("Password has been changed successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("User has been deleted successfully");
    }
}
