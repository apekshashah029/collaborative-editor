package com.example.websocket.security;

import com.example.websocket.dto.UserRequestDTO;
import com.example.websocket.exception.OAuthUserNotFoundException;
import com.example.websocket.exception.UnsupportedOAuthProviderException;
import com.example.websocket.service.CustomUserDetailService;
import com.example.websocket.util.CookieUtil;
import com.example.websocket.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil, CustomUserDetailService customUserDetailService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailService = customUserDetailService;
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

        try {
            customUserDetailService.loadUserByUsername(username);
            log.info("User already exists with username: {}", username);

        } catch (OAuthUserNotFoundException ex){

            UserRequestDTO userRequestDTO =
                    new UserRequestDTO(username, UUID.randomUUID().toString());

            customUserDetailService.doSignUp(userRequestDTO);
            log.info("New user registered with username: {}", username);
        }

        response.sendRedirect("/index.html");
    }

    private String extractUsername(String provider, OAuth2User user) {

        if ("google".equals(provider)) {
            return user.getAttribute("name");
        }

        if ("github".equals(provider)) {
            return user.getAttribute("login");
        }

        throw new UnsupportedOAuthProviderException(
                "OAuth provider not supported: " + provider
        );
    }
}
