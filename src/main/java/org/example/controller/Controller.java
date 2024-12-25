package org.example.controller;

import org.example.http.HttpRequest;
import org.example.http.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response);
}
