package com.example.websocket.controller;

import com.example.websocket.dto.AuthTokens;
import com.example.websocket.dto.UserRequestDTO;
import com.example.websocket.dto.UserResponseDTO;
import com.example.websocket.service.AuthService;
import com.example.websocket.service.CustomUserDetailService;
import com.example.websocket.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final CustomUserDetailService userService;

    public AuthController(AuthService authService,
                          CustomUserDetailService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserResponseDTO signup(@RequestBody UserRequestDTO dto) {
        return userService.doSignUp(dto);
    }
}
