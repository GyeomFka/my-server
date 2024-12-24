package org.example.webserver;

import org.example.util.GetStaticResource;
import org.example.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class RequestHandler extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
//		logger.info("ip={}", connection.getInetAddress());
//		logger.info("port={}", connection.getPort());
		try (InputStream in = connection.getInputStream();
			 OutputStream out = connection.getOutputStream()) {

			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);

			String requestUrl;
			StringBuilder requestInfo = new StringBuilder();
			String brLine;

			while ((brLine = br.readLine()) != null && !brLine.isEmpty()) {
				logger.info(brLine);
				requestInfo.append(brLine).append(System.lineSeparator());
			}

			requestUrl = StringUtil.getRequestStartLine(requestInfo.toString());

			HashMap<String, String> queryParam = null;

			if (StringUtil.isValidQueryString(requestUrl)) {
				queryParam = getQueryParam(requestUrl);
				logger.info("queryParam={}", queryParam);
			}

			if (queryParam != null && !queryParam.isEmpty()) {
				queryParam.forEach((key, value) -> logger.info(key + ": " + value));
			}

			byte[] body = null;

            if ("/index.html".equals(requestUrl) || "/".equals(requestUrl)) {
				body = GetStaticResource.getHtmlInfo("index");
			} else if ("/user/form.html".equals(requestUrl)) {
				body = GetStaticResource.getHtmlInfo("user/form");
			} else {
				body = GetStaticResource.getHtmlInfo("exception/not-found");
			}

			DataOutputStream dos = new DataOutputStream(out);
			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private static HashMap<String, String> getQueryParam(String requestUrl) {
		HashMap<String, String> returnMap = new HashMap<>();
		String[] parameterInfo = requestUrl.split("\\?");
		String queryString = parameterInfo[1];
		String[] pairs = queryString.split("&");
		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			returnMap.put(keyValue[0], (keyValue.length == 2) ? keyValue[1] : "");
		}
		return returnMap;
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
