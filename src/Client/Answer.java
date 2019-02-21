package Client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import Messages.FileBlockRequestMessage;
import Messages.FileDetails;
import Messages.FilePart;
import Messages.WordSearchMessage;

public class Answer implements Runnable {

	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String path;
	private Socket socket;

	public Answer(Socket socket, String path) {
		this.socket = socket;
		this.path = path;
		try {
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		File[] files = new File(path).listFiles();
		try {
			Object tmp = in.readObject();
			if (tmp instanceof WordSearchMessage) {
				WordSearchMessage msg = (WordSearchMessage) tmp;
				List<FileDetails> list = new ArrayList<FileDetails>();
				for (File f : files) {
					if (f.getName().toLowerCase().contains(msg.getKeyword().toLowerCase()))
						list.add(new FileDetails(f.getName(), f.length()));
				}
				out.writeObject(list);
			} else if (tmp instanceof FileBlockRequestMessage) {
				while (tmp != null) {
					FileBlockRequestMessage msg = (FileBlockRequestMessage) tmp;
					int lenght = msg.getLenght();
					int offset = msg.getOffset();
					;
					byte[] bytes = new byte[lenght];
					for (File f : files) {
						if (f.getName().equals(msg.getName())) {
							byte[] content = Files.readAllBytes(f.toPath());
							for (int j = 0; j < lenght; j++) {
								bytes[j] = content[offset + j];
							}
						}
					}
					out.writeObject(new FilePart(bytes));
					tmp = in.readObject();
				}
			}
			socket.close();
		} catch (NullPointerException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
}