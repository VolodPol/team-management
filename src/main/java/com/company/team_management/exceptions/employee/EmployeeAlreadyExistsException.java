package com.company.team_management.exceptions.employee;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmployeeAlreadyExistsException extends RuntimeException{
    private String message;

    public EmployeeAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
