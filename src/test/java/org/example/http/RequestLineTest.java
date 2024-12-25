package org.example.http;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RequestLineTest {

    @Test
    void CREATE_METHOD() {
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
        assertEquals("GET", line.getMethod());
        assertEquals("/index.html", line.getPath());

        line = new RequestLine("POST /index.html HTTP/1.1");
        assertEquals("/index.html", line.getPath());
    }

    @Test
    void CREATE_PATH_AND_PARAMS() {
        RequestLine line = new RequestLine("GET /user/create?userId=aksrua&password=1234 HTTP/1.1");
        assertEquals("GET", line.getMethod());
        assertEquals("/user/create", line.getPath());
        Map<String, String> params = line.getParams();
        assertEquals(2, params.size());
    }

}