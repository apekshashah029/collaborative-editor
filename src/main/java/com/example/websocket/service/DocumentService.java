package com.example.websocket.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.websocket.entity.Document;
import com.example.websocket.repository.DocumentRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DocumentService {

    private final DocumentRepository repository;

    private final Map<String, String> documentCache = new ConcurrentHashMap<>();


    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void loadAllDocuments() {
        for (Document doc : repository.findAll()) {
            documentCache.put(doc.getDocId(), doc.getContent());
        }
        log.info("Loaded documents into cache: " + documentCache.size());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public String saveOrUpdate(String docId, String content) {

        documentCache.put(docId, content);

        Document doc = repository.findById(docId)
                .orElse(new Document(docId, ""));

        doc.setContent(content);
        repository.save(doc);

        return doc.getContent();
    }

    @Transactional(readOnly = true)
    public String getContent(String docId) {
        return documentCache.getOrDefault(docId, "");
    }
}
