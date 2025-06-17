package com.example.task_manager.services;

import com.example.task_manager.dtos.request.user.UpdateUserPasswordRequestDto;
import com.example.task_manager.dtos.request.user.UserLoginRequestDto;
import com.example.task_manager.dtos.request.user.UserRequestDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;

public interface IUserService {
    public User
    public UserResponseDto create(UserRequestDto dto);
    public UserResponseDto update(String id, UserRequestDto dto);
    public void updatePassword(String id, UpdateUserPasswordRequestDto dto);
    public void delete(String id);
    public void login(UserLoginRequestDto dto);
}
