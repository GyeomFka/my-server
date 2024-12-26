package org.example.http;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private Map<String, Object> values = new HashMap<String, Object>();

    @Getter
    private String id;

    public HttpSession(String id) {
        this.id = id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void invalidate() {
        HttpSessions.remove(id);
    }
}