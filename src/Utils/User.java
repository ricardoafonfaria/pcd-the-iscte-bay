package Utils;

public class User {

	private String address;
	private int port;
	private String path;

	public User(String address, int port, String path) {
		super();
		this.address = address;
		this.port = port;
		this.path = path;
	}

	public User(String address, int port) {
		this.address = address;
		this.port = port;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return address + " " + port;
	}

}
