package com.example.websocket.service;

import com.example.websocket.entity.RefreshToken;
import com.example.websocket.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

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

}
