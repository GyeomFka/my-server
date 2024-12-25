package org.example.webserver;

import org.example.controller.Controller;
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

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		logger.info("ip={}", connection.getInetAddress());
		logger.info("port={}", connection.getPort());

		try (InputStream in = connection.getInputStream();
			 OutputStream out = connection.getOutputStream()) {

			HttpRequest request = new HttpRequest(in);
			HttpResponse response = new HttpResponse(out);
			Controller controller = RequestMapping.getController(request.getPath());

			if (controller == null) {
				String path = getDefaultPath(request.getPath());
				response.forward(path);
			} else {
				controller.service(request, response);
			}
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
