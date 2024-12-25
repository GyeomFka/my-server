package org.example.webserver;

import org.example.http.HttpRequest;
import org.example.http.HttpResponse;
import org.example.model.User;
import org.example.repository.Repository;
import org.example.util.HttpRequestUtils;
import org.example.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

public class RequestHandler extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	private Repository repository;

	public RequestHandler(Socket connectionSocket, Repository repository) {
		this.connection = connectionSocket;
		this.repository = repository;
	}

	public void run() {
		logger.info("ip={}", connection.getInetAddress());
		logger.info("port={}", connection.getPort());

		try (InputStream in = connection.getInputStream();
			 OutputStream out = connection.getOutputStream()) {

			HttpRequest request = new HttpRequest(in);
			HttpResponse response = new HttpResponse(out);
			String path = getDefaultPath(request.getPath());

			if ("/user/create".equals(path)) {
				repository.addUser(new User(request.getParameter("userId")
						, request.getParameter("password")
						, request.getParameter("name")
						, request.getParameter("email")));
				response.sendRedirect("/index.html");
			} else if ("/user/login".equals(path)) {
				User user = repository.findUserById(request.getParameter("userId"));
				if (user != null) {
					logger.info("loginUser={}", user.toString());
					if (user.getPassword().equals(request.getParameter("userId"))) { // 로그인 성공
						response.addHeader("Set-Cookie", "logined=true");
						response.sendRedirect("/index.html");
					} else { // 로그인 실패
						response.sendRedirect("/user/login-failed.html");
					}
				} else { // 로그인 실패
					response.sendRedirect("/user/login-failed.html");
				}
			} else if ("/user/list".equals(path)) {
				if (!isLogin(request.getHeader("Cookie"))) { // 추후 cookie값 조회 로직 구현
					response.sendRedirect("/user/login.html");
					return;
				}

				ArrayList<User> userList = repository.findAll();
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
			} else {
				response.forward(path);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private boolean isLogin(String cookieValue) {
		Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
		String value = cookies.get("logined");
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

	private String getDefaultPath(String path) {
		if (path.equals("/")) {
			return "/index.html";
		}
		return path;
	}
}
