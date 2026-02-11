package com.example.websocket.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class CookieConfig {

    @Value("${cookie.access.name}")
    private String accessName;

    @Value("${cookie.access.http-only}")
    private boolean httpOnly;

    @Value("${cookie.access.secure}")
    private boolean secure;

    @Value("${cookie.access.path}")
    private String accessPath;

    @Value("${cookie.access.max-age}")
    private int accessMaxAge;

    @Value("${cookie.refresh.name}")
    private String refreshName;

    @Value("${cookie.refresh.path}")
    private String refreshPath;

    @Value("${cookie.refresh.max-age}")
    private int refreshMaxAge;
}
