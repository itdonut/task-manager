package com.example.task_manager.services.impl;

import com.example.task_manager.dtos.request.user.RegisterUserDto;
import com.example.task_manager.dtos.request.user.UpdateUserPasswordDto;
import com.example.task_manager.dtos.request.user.LoginUserDto;
import com.example.task_manager.dtos.request.user.UserRequestDto;
import com.example.task_manager.dtos.response.user.UserResponseDto;
import com.example.task_manager.entities.User;
import com.example.task_manager.exceptions.ResourceAlreadyExistsException;
import com.example.task_manager.exceptions.ResourceDeletionNotAllowedException;
import com.example.task_manager.exceptions.ResourceNotFoundException;
import com.example.task_manager.exceptions.UserPasswordMismatchException;
import com.example.task_manager.mappers.UserMapper;
import com.example.task_manager.repositories.UserRepository;
import com.example.task_manager.services.ITaskService;
import com.example.task_manager.services.ITeamService;
import com.example.task_manager.services.IUserService;
import com.example.task_manager.utils.DateTimeUTC;
import com.example.task_manager.utils.PasswordUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final ITaskService taskService;
    private final ITeamService teamService;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, @Lazy ITaskService taskService, @Lazy ITeamService teamService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.teamService = teamService;
        this.userMapper = userMapper;
    }

    @Override
    public boolean isUserPresentById(String id) {
        return userRepository.existsById(id);
    }

    @Override
    public UserResponseDto getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "User with id=" + id + " doesn't exist",
                        DateTimeUTC.now()
                )
        );
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto register(RegisterUserDto dto) {
        String username = dto.getUsername();
        if (userRepository.findByUsername(username).isPresent()) {
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
        User user = this.getUserEntityById(id);

        User updatedUser = userMapper.toEntity(dto);
        updatedUser.setId(id);
        updatedUser.setPassword(user.getPassword());
        updatedUser.setCreatedAt(user.getCreatedAt());
        updatedUser.setModifiedAt(DateTimeUTC.now());

        return userMapper.toDto(userRepository.save(updatedUser));
    }

    @Override
    public void updatePassword(String id, UpdateUserPasswordDto dto) {
        User user = this.getUserEntityById(id);

        if (!PasswordUtil.verifyPassword(dto.getOldPassword(), user.getPassword())) {
            throw new UserPasswordMismatchException(
                    "Password does not match the one stored",
                    DateTimeUTC.now()
            );
        }

        user.setPassword(PasswordUtil.hashPassword(dto.getNewPassword()));
        user.setModifiedAt(DateTimeUTC.now());
        userRepository.save(user);
    }

    @Override
    public void delete(String id) {
        assertUserExistsById(id);

        if (teamService.getTeamsByUserId(id)
                .stream()
                .anyMatch(team -> Objects.equals(team.getOwnerId(), id))
        ) {
            throw new ResourceDeletionNotAllowedException(
                    "User with id=" + id + " cannot be deleted due to ownership of other resources",
                    DateTimeUTC.now()
            );
        }

        taskService.deleteUserTasksByUserId(id);
        taskService.unassignUserById(id);
        teamService.deleteUserFromAllTeamsById(id);
        userRepository.deleteById(id);
    }

    @Override
    public void login(LoginUserDto dto) {

    }

    @Override
    public void assertUserExistsById(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "User with id=" + id + " doesn't exist",
                    DateTimeUTC.now()
            );
        }
    }

    private User getUserEntityById(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "User with id=" + id + " doesn't exist",
                        DateTimeUTC.now()
                )
        );
    }
}
