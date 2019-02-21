package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;

import Messages.FileBlockRequestMessage;
import Messages.FileDetails;
import Messages.WordSearchMessage;
import Utils.User;

public class Client extends Observable {

	private static final int BLOCK_SIZE = 1024;

	private List<String> logs;
	private List<Request> requests;
	private Map<FileDetails, List<User>> map;
	private ServerSocket server;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private String address;
	private Integer port;
	private String path;
	private int progress;

	public Client(String[] args) {
		try {
			socket = new Socket(args[1], Integer.valueOf(args[2]));
			address = InetAddress.getLocalHost().toString()
					.substring(InetAddress.getLocalHost().toString().lastIndexOf("/") + 1);
			port = Integer.valueOf(args[3]);
			path = args[4];
			server = new ServerSocket(port);
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println("INSC " + args[0] + " " + address + " " + port);
			new ClientConnectionsHandler(server, path).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void search(String keyword) {
		map = new HashMap<FileDetails, List<User>>();
		requests = new ArrayList<Request>();
		out.println("CLT");
		try {
			while (true) {
				String msg = in.readLine();
				if (msg.startsWith("CLT")) {
					String[] args = msg.split(" ");
					if (!(args[1].equals(address) && Integer.valueOf(args[2]).equals(port))) {
						Request request = new Request(new WordSearchMessage(keyword),
								new User(args[1], Integer.valueOf(args[2])), map);
						request.start();
						requests.add(request);
					}
				} else if (msg.startsWith("END"))
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void download(FileDetails file) {
		try {
			progress = 0;
			byte[] bytes = new byte[(int) file.getSize()];
			List<User> users = map.get(file);
			int file_size = (int) file.getSize();
			int num_blocks = file_size / BLOCK_SIZE + 1;
			Queue<FileBlockRequestMessage> queue = new LinkedList<FileBlockRequestMessage>();
			logs = new ArrayList<String>();
			for (int i = 0; i < num_blocks; i++) {
				if (i == num_blocks - 1) {
					int lenght = file_size % BLOCK_SIZE;
					if (lenght > 0)
						queue.offer(new FileBlockRequestMessage(file.getName(), i * BLOCK_SIZE, lenght));
				} else
					queue.offer(new FileBlockRequestMessage(file.getName(), i * BLOCK_SIZE, BLOCK_SIZE));
			}
			requests = new ArrayList<Request>();
			users.forEach(user -> {
				Request request = new Request(queue, user, bytes, logs, this);
				request.start();
				requests.add(request);
			});
			for (Request r : requests) {
				r.join();
			}
			Files.write(Paths.get(path + "/" + file.getName()), bytes);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public List<String> getLogs() {
		return logs;
	}

	public Map<FileDetails, List<User>> getMap() {
		for (Request r : requests)
			try {
				r.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return map;
	}

	public synchronized void progressChanged() {
		progress++;
		this.setChanged();
		this.notifyObservers();
	}

	public synchronized int getProgress() {
		return progress;
	}
}
