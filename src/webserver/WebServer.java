package webserver;

import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

	private static final int DEFAULT_PORT = 8080;

	public static void main(String[] args) throws Exception {
		int port = 0;
		if (args == null || args.length == 0) {
			port = DEFAULT_PORT;
		} else {
			port = Integer.parseInt(args[0]);
		}

		try (ServerSocket listenSocket = new ServerSocket(port)) {
			System.out.println("port = " + port);

			Socket connection;
			int i = 0;
			while ((connection = listenSocket.accept()) != null) {
				System.out.println(i++);
				RequestHandler requestHandler = new RequestHandler(connection);
				requestHandler.start();
			}
		}
	}
}
