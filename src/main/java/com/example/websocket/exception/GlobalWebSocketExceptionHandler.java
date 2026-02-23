package com.example.websocket.exception;

import com.example.websocket.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalWebSocketExceptionHandler {

    @MessageExceptionHandler(ForbiddenException.class)
    @SendTo("/topic/errors")
    public ErrorResponse handleEditNotAllowed(
            ForbiddenException ex) {

        return new ErrorResponse(
                403,
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @MessageExceptionHandler(Exception.class)
    @SendTo("/topic/errors")
    public ErrorResponse handleGeneric(Exception ex) {

        return new ErrorResponse(
                500,
                ex.getMessage(),
                LocalDateTime.now()
        );
    }
}
