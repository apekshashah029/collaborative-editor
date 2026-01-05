package com.example.websocket.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "document")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    @Id
    @Column(name = "doc_id")
    private String docId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;

}
