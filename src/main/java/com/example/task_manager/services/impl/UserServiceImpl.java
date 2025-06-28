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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
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
        boolean exists = userRepository.existsById(id);
        log.info("[UserService] User with ID: {} exists: {}", id, exists);
        return exists;
    }

    @Override
    public UserResponseDto getUserById(String id) {
        log.info("[UserService] Attempting to fetch user with ID: {}", id);
        User user = this.getUserEntityById(id);
        log.info("[UserService] Successfully fetched user with ID: {}", id);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto register(RegisterUserDto dto) {
        String username = dto.getUsername();
        log.info("[UserService] Attempting to register user with username: {}", username);
        if (userRepository.findByUsername(username).isPresent()) {
            log.warn("[UserService] Registration failed: user with username '{}' already exists", username);
            throw new ResourceAlreadyExistsException(
                    "User with username=" + username + " already exists",
                    DateTimeUTC.now()
            );
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(PasswordUtil.hashPassword(dto.getPassword()));
        user.setCreatedAt(DateTimeUTC.now());
        user.setModifiedAt(DateTimeUTC.now());

        User saved = userRepository.save(user);
        log.info("[UserService] User registered successfully with ID: {}", saved.getId());
        return userMapper.toDto(saved);
    }

    @Override
    public UserResponseDto update(String id, UserRequestDto dto) {
        log.info("[UserService] Updating user with ID: {}", id);
        User user = this.getUserEntityById(id);

        User updatedUser = userMapper.toEntity(dto);
        updatedUser.setId(id);
        updatedUser.setPassword(user.getPassword());
        updatedUser.setCreatedAt(user.getCreatedAt());
        updatedUser.setModifiedAt(DateTimeUTC.now());

        User saved = userRepository.save(updatedUser);
        log.info("[UserService] User with ID: {} updated successfully", saved.getId());
        return userMapper.toDto(saved);
    }

    @Override
    public void updatePassword(String id, UpdateUserPasswordDto dto) {
        log.info("[UserService] Attempting to update password for user with ID: {}", id);
        User user = this.getUserEntityById(id);

        if (!PasswordUtil.verifyPassword(dto.getOldPassword(), user.getPassword())) {
            log.warn("[UserService] Password update failed: password mismatch for user with ID: {}", id);
            throw new UserPasswordMismatchException("Password does not match the one stored", DateTimeUTC.now());
        }

        user.setPassword(PasswordUtil.hashPassword(dto.getNewPassword()));
        user.setModifiedAt(DateTimeUTC.now());
        userRepository.save(user);
        log.info("[UserService] Password updated successfully for user with ID: {}", id);
    }

    @Override
    public void delete(String id) {
        log.info("[UserService] Attempting to delete user with ID: {}", id);
        assertUserExistsById(id);

        if (teamService.getTeamsByUserId(id)
                .stream()
                .anyMatch(team -> Objects.equals(team.getOwnerId(), id))
        ) {
            log.warn("[UserService] Deletion blocked: user with ID: {} is an owner of a team", id);
            throw new ResourceDeletionNotAllowedException(
                    "User with ID: " + id + " cannot be deleted due to ownership of other resources",
                    DateTimeUTC.now()
            );
        }

        taskService.deleteUserTasksByUserId(id);
        taskService.unassignUserById(id);
        teamService.deleteUserFromAllTeamsById(id);
        userRepository.deleteById(id);
        log.info("[UserService] User with ID {} deleted successfully", id);
    }

    @Override
    public void login(LoginUserDto dto) {

    }

    @Override
    public void assertUserExistsById(String id) {
        if (!userRepository.existsById(id)) {
            log.warn("[UserService] User with ID: {} does not exist", id);
            throw new ResourceNotFoundException("User with ID: " + id + " doesn't exist", DateTimeUTC.now());
        }
    }

    private User getUserEntityById(String id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.warn("[UserService] User with ID: {} not found", id);
            return new ResourceNotFoundException("User with ID: " + id + " doesn't exist", DateTimeUTC.now());
        });
    }
}
