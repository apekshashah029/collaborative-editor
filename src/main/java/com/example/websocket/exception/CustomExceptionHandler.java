package com.example.websocket.exception;

import com.example.websocket.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameExists(
            UsernameAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationError(
            UserRegistrationException ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UsernameNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Something went wrong",
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(UnsupportedOAuthProviderException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedProvider(
            UnsupportedOAuthProviderException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

}
