package com.company.team_management.exceptions.already_exists;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TaskAlreadyExistsException extends RuntimeException {
    private String message;

    public TaskAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
