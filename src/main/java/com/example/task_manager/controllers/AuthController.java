package com.example.task_manager.controllers;

import com.example.task_manager.dtos.request.user.LoginUserDto;
import com.example.task_manager.dtos.request.user.RegisterUserDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;
import com.example.task_manager.services.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final IUserService userService;
    private final HttpServletRequest request;

    public AuthController(IUserService userService, HttpServletRequest request) {
        this.userService = userService;
        this.request = request;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody RegisterUserDto dto) {
        log.info("[AuthController][{} {}] START registration for username: {}, email: {}",
                request.getMethod(), request.getRequestURI(), dto.getUsername(), dto.getEmail());
        UserResponseDto response = userService.register(dto);
        log.info("[AuthController][{} {}] SUCCESS registered user with ID: {}",
                request.getMethod(), request.getRequestURI(), response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginUserDto dto) {
        log.info("[AuthController][{} {}] START login attempt for username: {}",
                request.getMethod(), request.getRequestURI(), dto.getUsername());
        userService.login(dto);
        log.info("[AuthController][{} {}] SUCCESS login for username: {} was successful",
                request.getMethod(), request.getRequestURI(), dto.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body("Login successful");
    }
}
