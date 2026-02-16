package com.example.websocket.controller;

import com.example.websocket.entity.RefreshToken;
import com.example.websocket.exception.JwtInvalidTokenException;
import com.example.websocket.repository.RefreshTokenRepository;
import com.example.websocket.service.RefreshTokenService;
import com.example.websocket.util.CookieUtil;
import com.example.websocket.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class AuthJwtController {

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String refreshToken = cookieUtil.extractRefreshToken(request);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token missing");
        }

        String username =
                refreshTokenService.validateAndGetUsername(refreshToken);

        String newAccessToken = jwtUtil.generateAccessToken(username);
        cookieUtil.addAccessTokenCookie(response, newAccessToken);

        return ResponseEntity.ok("Access token refreshed");
    }
}
