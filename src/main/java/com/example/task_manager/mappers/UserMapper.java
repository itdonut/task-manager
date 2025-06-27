package com.example.task_manager.mappers;

import com.example.task_manager.dtos.request.user.RegisterUserDto;
import com.example.task_manager.dtos.request.user.UserRequestDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;
import com.example.task_manager.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequestDto dto) {
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .phone(dto.getPhone())
                .build();
    }

    public User toEntity(RegisterUserDto dto) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .build();
    }

    public UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
    }
}
