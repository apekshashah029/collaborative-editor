package com.example.websocket.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        String provider = oauthToken.getAuthorizedClientRegistrationId(); // either google or github
        OAuth2User oauthUser = oauthToken.getPrincipal();

        log.info("OAuth2 login successful via {}", provider);

        if ("google".equals(provider)) {
            handleGoogleLogin(oauthUser);
        } else if ("github".equals(provider)) {
            handleGithubLogin(oauthUser);
        }

        response.sendRedirect("/index.html");
    }

    private void handleGoogleLogin(OAuth2User user) {
        String email = user.getAttribute("email");
        String name = user.getAttribute("name");
        String googleId = user.getAttribute("sub");

        log.info("Google user -> email={}, name={}, id={}", email, name, googleId);
    }

    private void handleGithubLogin(OAuth2User user) {
        String username = user.getAttribute("login");
        String email = user.getAttribute("email"); // may be null if github haven't  attached email
        Integer githubId = user.getAttribute("id");

        log.info("GitHub user -> username={}, email={}, id={}", username, email, githubId);
    }
}
