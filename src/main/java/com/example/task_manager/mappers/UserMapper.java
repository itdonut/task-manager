package com.example.task_manager.mappers;

import com.example.task_manager.dtos.request.user.UserRequestDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;
import com.example.task_manager.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequestDto userRequestDto) {
        return User.builder()
                .id(userRequestDto.getId())
                .username(userRequestDto.getUsername())
                .email(userRequestDto.getEmail())
                .firstname(userRequestDto.getFirstname())
                .lastname(userRequestDto.getLastname())
                .phone(userRequestDto.getPhone())
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
