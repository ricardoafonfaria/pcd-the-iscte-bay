package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import Messages.FileBlockRequestMessage;
import Messages.FileDetails;
import Messages.FilePart;
import Messages.WordSearchMessage;
import Utils.User;

public class Request extends Thread {

	private Map<FileDetails, List<User>> map;
	private byte[] bytes;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private WordSearchMessage msg;
	private User user;
	private Queue<FileBlockRequestMessage> queue;
	private List<String> logs;
	private Socket socket;
	private Client client;

	public Request(WordSearchMessage msg, User user, Map<FileDetails, List<User>> map) {
		this.msg = msg;
		this.user = user;
		this.map = map;
		try {
			socket = new Socket(user.getAddress(), user.getPort());
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Request(Queue<FileBlockRequestMessage> queue, User user, byte[] bytes, List<String> logs, Client client) {
		this.queue = queue;
		this.bytes = bytes;
		this.user = user;
		this.logs = logs;
		this.client = client;
		try {
			socket = new Socket(user.getAddress(), user.getPort());
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		if (queue != null) {
			int num_blocks = 0;
			boolean error = false;
			while (true) {
				FileBlockRequestMessage msg;
				synchronized (queue) {
					msg = queue.poll();
				}
				if (msg == null)
					break;
				try {
					out.writeObject(msg);
				} catch (IOException e) {
					queue.add(msg);
					error = true;
				}
				if (!error) {
					try {
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								queue.add(msg);
								interrupt();
							}
						}, 5000);
						FilePart part = (FilePart) in.readObject();
						timer.cancel();
						byte[] answer = part.getBytes();
						int offset = msg.getOffset();
						for (int i = 0; i < answer.length; i++) {
							bytes[offset + i] = answer[i];
						}
						client.progressChanged();
						num_blocks++;
					} catch (ClassNotFoundException | IOException e) {
						queue.add(msg);
						interrupt();
					}
				}
			}
			if (num_blocks > 0)
				logs.add("[" + user.getAddress() + ":" + user.getPort() + "]" + " forneceu " + num_blocks + " partes");
			interrupt();

		} else {
			try {
				out.writeObject(msg);
				@SuppressWarnings("unchecked")
				ArrayList<FileDetails> msg = (ArrayList<FileDetails>) in.readObject();
				for (FileDetails f : msg) {
					boolean alreadyexists = false;
					synchronized (map) {
						for (FileDetails file : map.keySet()) {
							if (file.equals(f)) {
								map.get(file).add(user);
								alreadyexists = true;
								break;
							}
						}
						if (!alreadyexists) {
							List<User> list = new ArrayList<User>();
							list.add(user);
							map.put(f, list);
						}
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
