package webserver;

import java.io.*;
import java.net.Socket;

public class RequestHandler extends Thread {

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
//		System.out.println("ip = " + connection.getInetAddress());
//		System.out.println("port = " + connection.getPort());
		try (InputStream in = connection.getInputStream();
			 OutputStream out = connection.getOutputStream()) {

			String requestUrl = getRequestUrl(in);

			byte[] body = null;

            if (requestUrl != null && requestUrl.equals("/index.html")) {
				String indexHtml = "<html>";
				indexHtml += "<head></head>";
				indexHtml += "<body>indexHtml</body>";
				indexHtml += "</html>";
				body = indexHtml.getBytes();
            } else {
				String indexHtml = "<html>";
				indexHtml += "<head></head>";
				indexHtml += "<body>else body</body>";
				indexHtml += "</html>";
				body = indexHtml.getBytes();
			}

			DataOutputStream dos = new DataOutputStream(out);
			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private String getRequestUrl(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);
		int count = in.available();
		byte[] b = new byte[count];
		dis.read(b); //해당 코드 유무에 따라 결과값이 완전하게 달라진다.

		String inputString = new String(b);
		System.out.println("inputString = " + inputString);
		String requestUrl = null;

		String[] resultArray = inputString.split("\\r\\n");

		if (!resultArray[0].isBlank()) {
			String[] headerArray = resultArray[0].split(" ");
			requestUrl = headerArray[1];
		}

		return requestUrl;
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
