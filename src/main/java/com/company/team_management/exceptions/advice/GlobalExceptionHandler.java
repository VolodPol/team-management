package com.company.team_management.exceptions.advice;


import com.company.team_management.exceptions.ErrorResponse;
import com.company.team_management.exceptions.already_exists.EntityExistsException;
import com.company.team_management.exceptions.no_such.NoSuchEntityException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    // TODO: 11/19/2023 Implement handling of the MethodArgumentNotValidException
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        StringBuilder body = new StringBuilder("Constraint violations : \n");
        String collectedViolations = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("\n"));
        body.append(collectedViolations);

        return ResponseEntity.badRequest().body(body.toString());
    }

    @ExceptionHandler(value = {NoSuchEntityException.class, EntityExistsException.class})
    public ErrorResponse handleCommonExceptions(RuntimeException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }
}
