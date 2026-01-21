package com.example.websocket.exception;

public class OAuthUserNotFoundException extends RuntimeException{
    public OAuthUserNotFoundException(String message) {
        super(message);
    }
}
