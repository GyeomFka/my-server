package org.example.http;

import org.example.util.HttpRequestUtils;

import java.util.Map;

public class HttpCookie {
    private Map<String, String> cookies;

    public HttpCookie(String cookiesValue) {
        this.cookies = HttpRequestUtils.parseCookies(cookiesValue);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
