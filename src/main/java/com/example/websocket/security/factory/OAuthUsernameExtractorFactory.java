package com.example.websocket.security.factory;

import com.example.websocket.exception.UnsupportedOAuthProviderException;
import com.example.websocket.security.extractor.OAuthUsernameExtractor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OAuthUsernameExtractorFactory {

    private final Map<String, OAuthUsernameExtractor> strategies;

    public OAuthUsernameExtractorFactory(
            List<OAuthUsernameExtractor> extractors) {

        this.strategies = extractors.stream()
                .collect(Collectors.toMap(
                        OAuthUsernameExtractor::getProvider,
                        Function.identity()
                ));
    }

    public String extractUsername(String provider, OAuth2User user) {
        OAuthUsernameExtractor extractor = strategies.get(provider);

        if (extractor == null) {
            throw new UnsupportedOAuthProviderException(
                    "OAuth provider not supported: " + provider
            );
        }

        return extractor.extract(user);
    }
}
