package org.example.controller;

import org.example.http.HttpRequest;
import org.example.http.HttpResponse;
import org.example.model.User;
import org.example.repository.Repository;
import org.example.repository.impl.MemoryRepository;
import org.example.util.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

public class ListUserController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(ListUserController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (!isLogin(request.getHeader("Cookie"))) { // 추후 cookie값 조회 로직 구현
            response.sendRedirect("/user/login.html");
            return;
        }

        ArrayList<User> userList = MemoryRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : userList) {
            sb.append("<tr>");
            sb.append("	<td>" + user.getUserId() + "</td>");
            sb.append("	<td>" + user.getName() + "</td>");
            sb.append("	<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        response.forwardBody(sb.toString());
    }

    private boolean isLogin(String cookieValue) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }
}
