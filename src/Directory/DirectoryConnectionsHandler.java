package Directory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import Utils.User;

public class DirectoryConnectionsHandler extends Thread {

	BufferedReader in;
	PrintWriter out;
	List<User> online_users;
	User user;

	public DirectoryConnectionsHandler(Socket socket, List<User> online_users) {
		this.online_users = online_users;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				String msg = in.readLine();
				if (msg.startsWith("INSC")) {
					String[] args = msg.split(" ");
					user = new User(args[2], Integer.valueOf(args[3]));
					synchronized (online_users) {
						online_users.add(user);
					}
					System.out.println(msg);
				} else if (msg.startsWith("CLT")) {
					synchronized (online_users) {
						online_users.forEach(user -> out.println("CLT " + user.toString()));
					}
					out.println("END");
				}
			}
		} catch (IOException | NullPointerException e) {
			online_users.remove(user);
			if (user != null)
				System.out.println("LEFT " + user.toString());
		}
	}
}
