package com.example.websocket.exception;

public class JwtInvalidTokenException extends RuntimeException {
    public JwtInvalidTokenException(String message) {
        super(message);
    }
}
