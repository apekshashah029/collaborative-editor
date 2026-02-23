package com.example.websocket.service;

import com.example.websocket.entity.RefreshToken;
import com.example.websocket.exception.JwtInvalidTokenException;
import com.example.websocket.repository.RefreshTokenRepository;
import com.example.websocket.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;


    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpirationMs;

    public void saveRefreshToken(String token, String username) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .username(username)
                .expiryDate(
                        Instant.now().plusMillis(refreshTokenExpirationMs)
                )
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    public String validateAndGetUsername(String refreshToken) {

        RefreshToken tokenEntity = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() ->
                        new JwtInvalidTokenException("Invalid refresh token")
                );

        if (tokenEntity.isRevoked()
                || tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            throw new JwtInvalidTokenException("Invalid refresh token");
        }

        jwtUtil.validateToken(refreshToken);

        return tokenEntity.getUsername();
    }

}
