package com.example.websocket.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import com.example.websocket.config.CookieConfig;
import org.springframework.stereotype.Component;


@Component
public class CookieUtil {

    private final CookieConfig config;

    public CookieUtil(CookieConfig config) {
        this.config = config;
    }

    public void addAccessTokenCookie(HttpServletResponse res, String token) {
        Cookie cookie = new Cookie(config.name, token);
        cookie.setHttpOnly(config.httpOnly);
        cookie.setSecure(config.secure);
        cookie.setPath(config.path);
        cookie.setMaxAge(config.maxAge);
        res.addCookie(cookie);
    }

    public void clearAuthCookies(HttpServletResponse res) {
        Cookie access = new Cookie(config.name, null);
        access.setMaxAge(0);
        access.setPath(config.path);
        res.addCookie(access);
    }
}
