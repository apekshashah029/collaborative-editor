package com.example.websocket.filter;

import com.example.websocket.dto.ErrorResponse;
import com.example.websocket.exception.JwtInvalidTokenException;
import com.example.websocket.util.CookieUtil;
import com.example.websocket.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final CookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = cookieUtil.extractAccessToken(request);

            if (token != null) {
                if (!authenticate(token, request, response)) {
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean authenticate(String token,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {

        try {
            jwtUtil.validateToken(token);

            String username = jwtUtil.extractUsername(token);
            if (username == null || username.isBlank()) {
                return failAuth(response, "JWT does not contain username");
            }

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;

        } catch (UsernameNotFoundException ex) {
            return failAuth(response, "User not found for JWT token");
        } catch (JwtInvalidTokenException ex) {
            return failAuth(response, "Invalid JWT token");
        } catch (Exception ex) {
            return failAuth(response, "Authentication failed");
        }
    }

    private boolean failAuth(HttpServletResponse response, String message)
            throws IOException {

        SecurityContextHolder.clearContext();
        sendUnauthorizedResponse(response, message);
        return false;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response,
                                          String message) throws IOException {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpServletResponse.SC_UNAUTHORIZED,
                message,
                LocalDateTime.now()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter()
                .write(objectMapper.writeValueAsString(errorResponse));
    }
}
