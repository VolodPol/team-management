package com.company.team_management.controllers.auth;

import com.company.team_management.entities.users.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RegistrationRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
}
