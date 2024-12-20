package webserver;

import util.GetStaticResource;
import util.StringUtil;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class RequestHandler extends Thread {

	private Socket connection;
	private StringUtil util = new StringUtil();
	private GetStaticResource htmlViewr = new GetStaticResource();

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
//		System.out.println("ip = " + connection.getInetAddress());
//		System.out.println("port = " + connection.getPort());
		try (InputStream in = connection.getInputStream();
			 OutputStream out = connection.getOutputStream()) {

			String requestUrl = getUrlInfoFromRequest(in);

			byte[] body = null;

            if ("/index.html".equals(requestUrl)) {
				body = htmlViewr.getHtmlInfo("index");
			} else if ("/user/form.html".equals(requestUrl)) {
				body = htmlViewr.getHtmlInfo("user/form");
			} else {
				body = htmlViewr.getHtmlInfo("exception/notfound");
			}

			DataOutputStream dos = new DataOutputStream(out);
			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException e) {
			System.out.println("handler error : " + e.getMessage());
		}
	}

	private String getUrlInfoFromRequest(InputStream in) throws IOException {
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(isr);

		StringBuilder requestInfo = new StringBuilder();
		String brLine;
		while ((brLine = br.readLine()) != null) {
			requestInfo.append(brLine).append(System.lineSeparator());
			if (brLine.isEmpty()) {
				break;
			}
		}
		return util.getRequestStartLine(requestInfo.toString());
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			System.out.println("200header err : " + e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			System.out.println("responseBody err : " + e.getMessage());
		}
	}
}
