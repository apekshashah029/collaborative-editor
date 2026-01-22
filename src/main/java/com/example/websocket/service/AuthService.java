package com.example.websocket.service;

import com.example.websocket.dto.AuthTokens;
import com.example.websocket.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil
                       ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthTokens authenticate(String username, String password) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        String accessToken = jwtUtil.generateAccessToken(username);
        return new AuthTokens(accessToken);
    }

}
