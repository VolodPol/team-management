package com.company.team_management.exceptions.advice;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ViolationErrorResponse {
    private final List<Violation> violations;

    public ViolationErrorResponse() {
        violations = new ArrayList<>();
    }

    public void addViolation(Violation violation) {
        this.violations.add(violation);
    }
}
