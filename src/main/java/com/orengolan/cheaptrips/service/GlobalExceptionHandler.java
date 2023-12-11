package com.orengolan.cheaptrips.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * The {@code GlobalExceptionHandler} class is a controller advice that handles global exception scenarios across the application.
 * It provides methods to handle specific types of exceptions, ensuring consistent and meaningful responses to clients for various error situations.
 *
 * Key Features:
 * - Utilizes Spring's {@code @ControllerAdvice} annotation to provide centralized exception handling for all controllers.
 * - Defines methods to handle specific exception types, including bad requests, internal server errors, JSON processing errors,
 *   and validation errors using {@code @ExceptionHandler} annotations.
 * - Responds with appropriate HTTP status codes and error messages to ensure consistent and informative error handling.
 * - Implements a custom method {@code printTrack} to print relevant information about the error, including the caller class,
 *   caller method, and line number, enhancing debugging capabilities.
 *
 * Example Usage:
 * The class is utilized to handle exceptions thrown during the execution of controller methods, providing consistent error responses
 * to clients. It ensures that different types of exceptions, such as bad requests, internal server errors, JSON processing errors,
 * and validation errors, are appropriately handled and result in clear error messages.
 *
 * Note: This class plays a crucial role in maintaining a standardized approach to exception handling, improving the overall reliability
 * and user experience of the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends Exception {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<String> handleRateLimitExceededException(RateLimitExceededException e) {
        printTrack(e);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded: " + e.getMessage());
    }

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
