package com.company.team_management.exceptions.no_such;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoSuchTaskException extends RuntimeException {
    private String message;

    public NoSuchTaskException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
