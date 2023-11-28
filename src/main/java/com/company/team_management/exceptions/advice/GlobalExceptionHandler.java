package com.company.team_management.exceptions.advice;


import com.company.team_management.exceptions.already_exists.EntityExistsException;
import com.company.team_management.exceptions.no_such.NoSuchEntityException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ViolationErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        ViolationErrorResponse errorResponse = new ViolationErrorResponse();
        e.getConstraintViolations()
                .forEach(violation -> errorResponse.addViolation(
                        new Violation(violation.getPropertyPath().toString(), violation.getMessage()))
                );

        return errorResponse;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ViolationErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        ViolationErrorResponse errorResponse = new ViolationErrorResponse();
        e.getFieldErrors().forEach(
                error -> errorResponse.addViolation(
                        new Violation(error.getField(), error.getDefaultMessage())
                )
        );
        return errorResponse;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = {NoSuchEntityException.class, EntityExistsException.class})
    public ErrorResponse handleCommonExceptions(RuntimeException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }
}
