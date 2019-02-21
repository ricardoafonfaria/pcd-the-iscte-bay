package Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Utils.ThreadPool;

public class ClientConnectionsHandler extends Thread {

	private static final int THREAD_MAX = 5;
	private ServerSocket server;
	private String path;

	public ClientConnectionsHandler(ServerSocket server, String path) {
		this.server = server;
		this.path = path;
	}

	@Override
	public void run() {
		ThreadPool pool = new ThreadPool(THREAD_MAX);
		while (true) {
			try {
				Socket socket = server.accept();
				pool.submit(new Answer(socket, path));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
