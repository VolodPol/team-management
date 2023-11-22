package com.company.team_management.exceptions.no_such;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoSuchEntityException extends RuntimeException {
    private String message;

    public NoSuchEntityException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
