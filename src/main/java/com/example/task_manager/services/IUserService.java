package com.example.task_manager.services;

import com.example.task_manager.dtos.request.user.UpdateUserPasswordDto;
import com.example.task_manager.dtos.request.user.LoginUserDto;
import com.example.task_manager.dtos.request.user.UserRequestDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;

public interface IUserService {
    public boolean isUserPresentById(String id);
    public UserResponseDto getUserById(String id);
    public UserResponseDto create(UserRequestDto dto);
    public UserResponseDto update(String id, UserRequestDto dto);
    public void updatePassword(String id, UpdateUserPasswordDto dto);
    public void delete(String id);
    public void login(LoginUserDto dto);
    public void assertUserExistsById(String id);
}
