package org.example.webserver;

import org.example.http.HttpRequest;
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
			String path = getDefaultPath(request.getPath());

			if ("/user/create".equals(path)) {
				repository.addUser(new User(request.getParameter("userId")
						, request.getParameter("password")
						, request.getParameter("name")
						, request.getParameter("email")));
				response302Header(new DataOutputStream(out));
			} else if ("/user/login".equals(path)) {
				User user = repository.findUserById(request.getParameter("userId"));
				if (user != null) {
					logger.info("loginUser={}", user.toString());
					if (user.getPassword().equals(request.getParameter("userId"))) { // 로그인 성공
						DataOutputStream dos = new DataOutputStream(out);
						response302LoginSuccessHeader(dos);
					} else { // 로그인 실패
						responseResource(out, "/user/login-failed.html");
					}
				} else { // 로그인 실패
					responseResource(out, "/user/login-failed.html");
				}
			} else if ("/user/list.html".equals(path)) {
				if (!isLogin(request.getHeader("Cookie"))) { // 추후 cookie값 조회 로직 구현
					responseResource(out, "/user/login.html");
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

				byte[] body = sb.toString().getBytes();
				DataOutputStream dos = new DataOutputStream(out);
				response200Header(dos, body.length);
				responseBody(dos, body);
			} else {
//				responseResource(out, URI);
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

	private void responseResource(OutputStream out, String uri) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		if ("/".equals(uri)) uri = "index.html";
		byte[] body = Files.readAllBytes(new File("./webapp/" + uri).toPath());
		response200Header(dos, body.length);
		responseBody(dos, body);
	}

	private int getContentLength(String brLine) {
		String[] headerTokens = brLine.split(":");
		return Integer.parseInt(headerTokens[1].trim());
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private void response302LoginSuccessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
			logger.error(e.getMessage());
        }
    }

	private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
			logger.error(e.getMessage());
        }
    }

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private String getDefaultPath(String path) {
		if (path.equals("/")) {
			return "/index.html";
		}
		return path;
	}
}
