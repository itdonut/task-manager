package com.example.task_manager.services.impl;

import com.example.task_manager.dtos.request.user.UpdateUserPasswordRequestDto;
import com.example.task_manager.dtos.request.user.UserLoginRequestDto;
import com.example.task_manager.dtos.request.user.UserRequestDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;
import com.example.task_manager.entities.User;
import com.example.task_manager.exceptions.ResourceAlreadyExistsException;
import com.example.task_manager.exceptions.ResourceNotFoundException;
import com.example.task_manager.exceptions.UserPasswordMismatchException;
import com.example.task_manager.mappers.UserMapper;
import com.example.task_manager.repositories.UserRepository;
import com.example.task_manager.services.IUserService;
import com.example.task_manager.utils.DateTimeUTC;
import com.example.task_manager.utils.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDto create(UserRequestDto dto) {
        String username = dto.getUsername();
        if (userRepository.getUserByUsername(username).isPresent()) {
            throw new ResourceAlreadyExistsException(
                    "User with username=" + username + " already exists",
                    DateTimeUTC.now()
            );
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(PasswordUtil.hashPassword(dto.getPassword()));
        user.setCreatedAt(DateTimeUTC.now());
        user.setModifiedAt(DateTimeUTC.now());
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto update(String id, UserRequestDto dto) {
        User user = userRepository.getUserById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id=" + id + " doesn't exist", DateTimeUTC.now())
        );

        User updatedUser = userMapper.toEntity(dto);
        updatedUser.setId(id);
        updatedUser.setPassword(user.getPassword());
        updatedUser.setCreatedAt(user.getCreatedAt());
        updatedUser.setModifiedAt(DateTimeUTC.now());

        return userMapper.toDto(userRepository.save(updatedUser));
    }

    @Override
    public void updatePassword(String id, UpdateUserPasswordRequestDto dto) {
        User user = userRepository.getUserById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id=" + id + " doesn't exist", DateTimeUTC.now())
        );

        if (!PasswordUtil.verifyPassword(dto.getOldPassword(), user.getPassword())) {
            throw new UserPasswordMismatchException("Password does not match the one stored", DateTimeUTC.now());
        }

        user.setPassword(PasswordUtil.hashPassword(dto.getNewPassword()));
        user.setModifiedAt(DateTimeUTC.now());
        userRepository.save(user);
    }

    @Override
    public void delete(String id) {
        if (userRepository.getUserById(id).isEmpty())
            throw new ResourceNotFoundException("User with id=" + id + " doesn't exist", DateTimeUTC.now());
        userRepository.deleteById(id);
    }

    @Override
    public void login(UserLoginRequestDto dto) {

    }
}
