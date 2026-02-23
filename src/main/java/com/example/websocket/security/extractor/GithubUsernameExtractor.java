package com.example.websocket.security.extractor;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class GithubUsernameExtractor implements OAuthUsernameExtractor {

    @Override
    public String getProvider() {
        return "github";
    }

    @Override
    public String extract(OAuth2User user) {
        return user.getAttribute("login");
    }
}
