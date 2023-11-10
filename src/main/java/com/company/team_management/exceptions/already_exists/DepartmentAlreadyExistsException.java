package com.company.team_management.exceptions.already_exists;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DepartmentAlreadyExistsException extends RuntimeException {
    private String message;

    public DepartmentAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
