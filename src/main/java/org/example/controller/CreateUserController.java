package org.example.controller;

import org.example.http.HttpRequest;
import org.example.http.HttpResponse;
import org.example.model.User;
import org.example.repository.Repository;
import org.example.repository.impl.MemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(CreateUserController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        MemoryRepository.addUser(new User(request.getParameter("userId")
                , request.getParameter("password")
                , request.getParameter("name")
                , request.getParameter("email")));
        response.sendRedirect("/index.html");    }
}
