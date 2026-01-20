package com.example.websocket.security;

import com.example.websocket.util.CookieUtil;
import com.example.websocket.util.JwtUtil;
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

    private final JwtUtil jwtUtil;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;

        String provider = oauthToken.getAuthorizedClientRegistrationId();
        OAuth2User oauthUser = oauthToken.getPrincipal();

        log.info("OAuth2 login successful via {}", provider);

        String username = extractUsername(provider, oauthUser);

        String jwt = jwtUtil.generateAccessToken(username);
        CookieUtil.addAccessTokenCookie(response, jwt);

        log.info(jwt);

        response.sendRedirect("/index.html");
    }

    private String extractUsername(String provider, OAuth2User user) {

        if ("google".equals(provider)) {
            return user.getAttribute("name");
        }

        if ("github".equals(provider)) {
            return user.getAttribute("login");
        }

        throw new IllegalArgumentException("Unsupported provider");
    }
}
