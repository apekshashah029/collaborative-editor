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
        Cookie cookie = new Cookie(config.getAccessName(), token);
        cookie.setHttpOnly(config.isHttpOnly());
        cookie.setSecure(config.isSecure());
        cookie.setPath(config.getAccessPath());
        cookie.setMaxAge(config.getAccessMaxAge());
        res.addCookie(cookie);
    }

    public void addRefreshTokenCookie(HttpServletResponse res, String token) {
        Cookie cookie = new Cookie(config.getRefreshName(), token);
        cookie.setHttpOnly(config.isHttpOnly());
        cookie.setSecure(config.isSecure());
        cookie.setPath(config.getRefreshPath());
        cookie.setMaxAge(config.getRefreshMaxAge());
        res.addCookie(cookie);
    }

    public void clearAuthCookies(HttpServletResponse res) {
        clearCookie(res, config.getAccessName(), config.getAccessPath());
        clearCookie(res, config.getRefreshName(), config.getRefreshPath());
    }

    private void clearCookie(HttpServletResponse res, String name, String path) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath(path);
        cookie.setHttpOnly(config.isHttpOnly());
        cookie.setSecure(config.isSecure());
        res.addCookie(cookie);
    }
}
