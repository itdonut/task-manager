package com.example.task_manager.dtos.request.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    private String id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String firstname;
    private String lastname;
}
