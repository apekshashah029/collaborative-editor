package com.example.websocket.controller;

import com.example.websocket.exception.DocumentEditNotAllowedException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;

import com.example.websocket.service.DocumentService;

@Controller
public class TypingController {

    private final DocumentService documentService;

    public TypingController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @MessageMapping("/typing/{docId}")
    @SendTo("/topic/typing/{docId}")
    public String typing(
            @DestinationVariable String docId,
            @Payload(required = false) String text,
            Authentication authentication) {

        if (text == null) {
            return documentService.getContent(docId);
        }

        // PBAC + RBAC
        // User -> can view document
        // Admin -> can edit document
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new DocumentEditNotAllowedException(
                    "Only admins are allowed to edit documents"
            );
        }

        return documentService.saveOrUpdate(docId, text);
    }
}
