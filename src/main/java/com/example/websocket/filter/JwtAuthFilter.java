package com.example.websocket.filter;

import com.example.websocket.dto.ErrorResponse;
import com.example.websocket.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(JwtUtil jwtUtil,
                         UserDetailsService userDetailsService,ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null
                && request.getCookies() != null) {

            for (Cookie cookie : request.getCookies()) {

                if ("access_token".equals(cookie.getName())) {

                    String token = cookie.getValue();
                    if (token == null) {
                        sendUnauthorizedResponse(response, "Invalid or expired JWT token");
                        return;
                    }
                    if (!authenticate(token, request, response)) {
                        return;
                    }

                    break;
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

            if (username == null) {
                sendUnauthorizedResponse(response, "JWT does not contain username");
                return false;
            }

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    buildAuthentication(userDetails, request);

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            return true;

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            sendUnauthorizedResponse(response, "Authentication failed");
            return false;
        }
    }

    private UsernamePasswordAuthenticationToken buildAuthentication(
            UserDetails userDetails,
            HttpServletRequest request) {

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

        return authentication;
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
