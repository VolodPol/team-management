package com.company.team_management.exceptions.already_exists;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProjectAlreadyExistsException extends RuntimeException {
    private String message;

    public ProjectAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
