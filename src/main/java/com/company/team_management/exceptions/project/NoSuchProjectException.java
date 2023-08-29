package com.company.team_management.exceptions.project;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoSuchProjectException extends RuntimeException {
    private String message;

    public NoSuchProjectException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
