package com.example.task_manager.dtos.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String email;
    private String id;
    private String username;
    private String phone;
    private String firstname;
    private String lastname;
}
