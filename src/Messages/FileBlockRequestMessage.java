package Messages;

import java.io.Serializable;

public class FileBlockRequestMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private int offset;
	private int lenght;

	public FileBlockRequestMessage(String name, int offset, int lenght) {
		this.name = name;
		this.offset = offset;
		this.lenght = lenght;
	}

	public String getName() {
		return name;
	}

	public int getOffset() {
		return offset;
	}

	public int getLenght() {
		return lenght;
	}

}
