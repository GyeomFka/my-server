package org.example.webserver;

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

			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			String line = br.readLine();
			if (line == null) {
				return;
			}

			logger.info("request={}", line);
			String[] requestInfos = line.split(" ");

			String HTTP_METHOD = requestInfos[0];
			String URI = requestInfos[1];
			String VERSION = requestInfos[2];

			int contentLength = 0;
			boolean isLogined = false;
			while (!line.equals("")) {
				line = br.readLine();
				logger.info("header={}", line);
				if ("POST".equals(HTTP_METHOD) && line.startsWith("Content-Length")) { //만약 get방식 조회 search parameter에 Content-Length 가 포함되어있으면 어떻게함 ?
					contentLength = getContentLength(line);
				}

				if (line.contains("Cookie")) {
					logger.info("cookie line={}", line.toString());
					isLogined = isLogin(line);
				}
			}

			logger.info(" *** 현재 회원 수={}", repository.findAll().size() + "명 *** ");

			if ("/user/create".equals(URI)) {
				String queryString = IOUtils.readData(br, contentLength);
				Map<String, String> paramMap = HttpRequestUtils.parseQueryString(queryString);
				logger.info("paramMap={}", paramMap);
				repository.addUser(new User(paramMap.get("userId"), paramMap.get("password"), paramMap.get("name")
						, paramMap.get("email")));
				response302Header(new DataOutputStream(out));
			} else if ("/user/login".equals(URI)) {
				String queryString = IOUtils.readData(br, contentLength);
				Map<String, String> paramMap = HttpRequestUtils.parseQueryString(queryString);
				User user = repository.findUserById(paramMap.get("userId"));
				if (user != null) {
					logger.info("loginUser={}", user.toString());
					if (user.getPassword().equals(paramMap.get("password"))) { // 로그인 성공
						DataOutputStream dos = new DataOutputStream(out);
						response302LoginSuccessHeader(dos);
					} else { // 로그인 실패
						responseResource(out, "/user/login-failed.html");
					}
				} else { // 로그인 실패
					responseResource(out, "/user/login-failed.html");
				}
			} else if ("/user/list.html".equals(URI)) {
				if (!isLogined) {
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
				responseResource(out, URI);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private boolean isLogin(String line) {
		String[] headerTokens = line.split(":");
		Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
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
}
