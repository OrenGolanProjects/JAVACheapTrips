package com.orengolan.cheaptrips.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends Exception {

    @ExceptionHandler({NumberFormatException.class, IllegalArgumentException.class,IllegalStateException.class})
    public ResponseEntity<String> handleBadRequestException(Exception e) {
        printTrack(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request: " + e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleInternalServerError(Exception e) {
        printTrack(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
    }

    @ExceptionHandler({JsonProcessingException.class})
    public ResponseEntity<String> handleJsonProcessingException(Exception e) {
        printTrack(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing JSON: " + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Validation error: " + ex.getMessage());
    }

    private void printTrack(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement caller : stackTrace) {
            if (caller.getClassName().startsWith("com.orengolan.CheapTripsResponse")) {
                System.out.println("\u001B[31mCaller Class: " + caller.getClassName());
                System.out.println("Caller Method: " + caller.getMethodName());
                System.out.println("Caller Line Number: " + caller.getLineNumber() + "\u001B[0m"); // Reset color
            }
        }
        System.out.println("\u001B[31mError Message: " + e.getMessage() + "\u001B[0m"); // Reset color
    }


}
