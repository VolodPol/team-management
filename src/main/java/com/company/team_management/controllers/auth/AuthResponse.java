package com.company.team_management.controllers.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    @JsonProperty("access_token")
    private String token;
    @JsonProperty("refresh_token")
    private String refreshToken;
}
