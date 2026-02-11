package com.example.websocket.entity;

import com.example.websocket.entity.type.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    private UUID uid;

    private String username;

    @Column(nullable = true)
    private String password;

    private String refresh_token;

    @Enumerated(EnumType.STRING)
    private Role role;
}
