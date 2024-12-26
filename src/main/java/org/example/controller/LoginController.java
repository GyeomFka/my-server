package org.example.controller;

import org.example.http.HttpRequest;
import org.example.http.HttpResponse;
import org.example.model.User;
import org.example.repository.Repository;
import org.example.repository.impl.MemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        User user = MemoryRepository.findUserById(request.getParameter("userId"));
        if (user != null) {
            logger.info("loginUser={}", user.toString());
            if (user.getPassword().equals(request.getParameter("password"))) { // 로그인 성공
                response.addHeader("Set-Cookie", "logined=true");
                response.sendRedirect("/index.html");
            } else { // 로그인 실패
                response.sendRedirect("/user/login-failed.html");
            }
        } else { // 로그인 실패
            response.sendRedirect("/user/login-failed.html");
        }
    }
}

