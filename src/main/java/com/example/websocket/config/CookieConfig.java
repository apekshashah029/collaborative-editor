package com.example.websocket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieConfig {

    @Value("${cookie.access.name}")
    public String name;

    @Value("${cookie.access.http-only}")
    public boolean httpOnly;

    @Value("${cookie.access.secure}")
    public boolean secure;

    @Value("${cookie.access.path}")
    public String path;

    @Value("${cookie.access.max-age}")
    public int maxAge;
}
