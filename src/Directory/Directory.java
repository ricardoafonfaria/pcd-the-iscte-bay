package Directory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import Utils.User;

public class Directory {

	private int PORT = 8080;
	private ServerSocket server;
	private List<User> online_users;

	public Directory() {
		try {
			server = new ServerSocket(PORT);
			online_users = new LinkedList<User>();
			System.out.println(">< Directory Ready ><");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void start() {
		while (true) {
			try {
				Socket socket = server.accept();
				new DirectoryConnectionsHandler(socket, online_users).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Directory directory = new Directory();
		directory.start();
	}
}
