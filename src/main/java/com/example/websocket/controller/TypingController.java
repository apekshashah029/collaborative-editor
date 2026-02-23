package com.example.websocket.controller;

//import com.example.websocket.authorization.DocumentAuthorizationService;
import com.example.websocket.exception.ForbiddenException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.example.websocket.service.DocumentService;

@Controller
@AllArgsConstructor
public class TypingController {

    private final DocumentService documentService;

    @MessageMapping("/typing/{docId}")
    @SendTo("/topic/typing/{docId}")
    public String typing(
            @DestinationVariable String docId,
            @Payload(required = false) String text,
            Authentication authentication) {

        if (text == null) {
            return documentService.getContent(docId);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // PBAC + RBAC
        // User -> can view document
        // Admin -> can edit document

        return documentService.saveOrUpdate(docId, text);
    }
}
