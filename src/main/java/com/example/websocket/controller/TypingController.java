package com.example.websocket.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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
            @Payload(required = false) String text) {

        if (text == null) {
            return documentService.getContent(docId);
        }

        return documentService.saveOrUpdate(docId, text);
    }
}
