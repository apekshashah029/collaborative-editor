package com.example.websocket.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void addAccessTokenCookie(
            HttpServletResponse res, String token) {

        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(15 * 60);

        res.addCookie(cookie);
    }


    public static void clearAuthCookies(HttpServletResponse res) {
        Cookie access = new Cookie("access_token", null);
        access.setMaxAge(0);
        access.setPath("/");

        res.addCookie(access);
    }
}
