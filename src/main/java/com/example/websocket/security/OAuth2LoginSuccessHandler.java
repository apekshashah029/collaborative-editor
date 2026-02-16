package com.example.websocket.security;

import com.example.websocket.dto.LoginRequestDTO;
import com.example.websocket.exception.UnsupportedOAuthProviderException;
import com.example.websocket.security.factory.OAuthUsernameExtractorFactory;
import com.example.websocket.service.CustomUserDetailService;
import com.example.websocket.service.RefreshTokenService;
import com.example.websocket.util.CookieUtil;
import com.example.websocket.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final CustomUserDetailService customUserDetailService;
    private final RefreshTokenService refreshTokenService;
    private final OAuthUsernameExtractorFactory usernameExtractorFactory;

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

        String username =
                usernameExtractorFactory.extractUsername(provider, oauthUser);

        ensureUserExists(username);

        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        refreshTokenService.saveRefreshToken(refreshToken, username);

        cookieUtil.addAccessTokenCookie(response, accessToken);
        cookieUtil.addRefreshTokenCookie(response, refreshToken);

        response.sendRedirect("/index.html");
    }

    private void ensureUserExists(String username) {

        try {
            customUserDetailService.loadUserByUsername(username);
            log.info("User already exists: {}", username);

        } catch (UsernameNotFoundException ex) {

            LoginRequestDTO signupRequest =
                    new LoginRequestDTO(username, null);

            customUserDetailService.doSignUp(signupRequest, null);
            log.info("New OAuth user registered: {}", username);
        }
    }
}
