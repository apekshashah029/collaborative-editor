package com.example.websocket.exception;

public class DocumentEditNotAllowedException extends RuntimeException {

    public DocumentEditNotAllowedException(String message) {
        super(message);
    }
}
