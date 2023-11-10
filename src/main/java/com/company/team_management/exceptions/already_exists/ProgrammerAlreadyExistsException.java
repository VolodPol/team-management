package com.company.team_management.exceptions.already_exists;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProgrammerAlreadyExistsException extends RuntimeException{
    private String message;

    public ProgrammerAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
