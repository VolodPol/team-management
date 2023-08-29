package com.company.team_management.exceptions.employee;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoSuchEmployeeException extends RuntimeException {
    private String message;

    public NoSuchEmployeeException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
