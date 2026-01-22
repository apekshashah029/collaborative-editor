package com.example.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRequestDTO {
    private final String username;

    private final String password;
}
