package com.example.websocket.repository;

import com.example.websocket.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document,String> {

}
