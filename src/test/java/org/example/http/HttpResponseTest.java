package org.example.http;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    void RESPONSE_FORWARD() throws Exception {
        HttpResponse response = new HttpResponse(createOutputStream("Http_Forward.txt"));
        response.forward("/index.html");
    }

    @Test
    public void responseRedirect() throws Exception {
        HttpResponse response = new HttpResponse(createOutputStream("Http_Redirect.txt"));
        response.sendRedirect("/index.html");
    }

    @Test
    public void responseCookies() throws Exception {
        HttpResponse response = new HttpResponse(createOutputStream("Http_Cookie.txt"));
        response.addHeader("Set-Cookie", "logined=true");
        response.sendRedirect("/index.html");
    }

    private OutputStream createOutputStream(String filename) throws FileNotFoundException {
        return new FileOutputStream(new File(testDirectory + filename));
    }
}