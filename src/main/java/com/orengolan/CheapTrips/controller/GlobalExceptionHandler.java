package com.orengolan.CheapTrips.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends Exception {

    @ExceptionHandler({ChangeSetPersister.NotFoundException.class})
    public ResponseEntity<String> handleResourceNotFoundException(Exception e) {
        printTrack(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found: " + e.getMessage());
    }
    @ExceptionHandler({NumberFormatException.class, IllegalArgumentException.class})
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

    private void printTrack(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement caller : stackTrace) {
            if (caller.getClassName().startsWith("com.orengolan.CheapTrips")) {
                System.out.println("\u001B[31mCaller Class: " + caller.getClassName());
                System.out.println("Caller Method: " + caller.getMethodName());
                System.out.println("Caller Line Number: " + caller.getLineNumber() + "\u001B[0m"); // Reset color
            }
        }
        System.out.println("\u001B[31mError Message: " + e.getMessage() + "\u001B[0m"); // Reset color
    }


}
