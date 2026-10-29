
package com.incubyte.salarymanagement;

import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(
            ResponseStatusException exception) {

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(Map.of(
                        "error", "Request failed",
                        "message", exception.getReason() != null
                                ? exception.getReason()
                                : "An error occurred"
                ));
    }

    
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, String>> handleValidationErrors(
        MethodArgumentNotValidException exception) {

    String message = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " +
                    error.getDefaultMessage())
            .collect(Collectors.joining("; "));

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                    "error", "Validation failed",
                    "message", message
            ));
}

}
