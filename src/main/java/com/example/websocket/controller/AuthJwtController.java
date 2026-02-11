package com.example.websocket.controller;

import com.example.websocket.entity.RefreshToken;
import com.example.websocket.repository.RefreshTokenRepository;
import com.example.websocket.util.CookieUtil;
import com.example.websocket.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class AuthJwtController {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {


        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token missing");
        }

        RefreshToken tokenEntity = refreshTokenRepository
                .findByToken(refreshToken)
                .orElse(null);

        if (tokenEntity == null
                || tokenEntity.isRevoked()
                || tokenEntity.getExpiryDate().isBefore(Instant.now())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid refresh token");
        }

        jwtUtil.validateToken(refreshToken);

        String username = tokenEntity.getUsername();
        String newAccessToken = jwtUtil.generateAccessToken(username);

        cookieUtil.addAccessTokenCookie(response, newAccessToken);

        return ResponseEntity.ok("Access token refreshed");
    }
}
