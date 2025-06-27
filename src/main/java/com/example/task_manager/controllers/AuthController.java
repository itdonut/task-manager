package com.example.task_manager.controllers;

import com.example.task_manager.dtos.request.user.LoginUserDto;
import com.example.task_manager.dtos.request.user.RegisterUserDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;
import com.example.task_manager.services.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final IUserService userService;

    public AuthController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody RegisterUserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginUserDto dto) {
        userService.login(dto);
        return ResponseEntity.status(HttpStatus.OK).body("Login successful");
    }
}
