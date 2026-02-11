package com.example.websocket.security.extractor;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuthUsernameExtractor {
        String getProvider();
        String extract(OAuth2User user);
}
