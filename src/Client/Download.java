package Client;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import Messages.FileDetails;

public class Download extends SwingWorker<Void, Void> {

	private int inicio;
	private Client client;
	private FileDetails file;
	private JFrame frame;
	private JButton download;

	public Download(Client client, FileDetails file, JFrame frame, JButton download) {
		this.client = client;
		this.file = file;
		this.frame = frame;
		this.download = download;
		inicio = (int) (System.currentTimeMillis() / 1000);
	}

	@Override
	protected Void doInBackground() throws Exception {
		client.download(file);
		return null;
	}

	@Override
	protected void done() {
		int fim = (int) (System.currentTimeMillis() / 1000);
		String log = "";
		for (String s : client.getLogs())
			log += s + "\n";
		log += "Tempo decorrido: " + (fim - inicio) + " s";
		JOptionPane.showMessageDialog(frame, log, "Descarga completa", JOptionPane.PLAIN_MESSAGE);
		download.setEnabled(true);
	}

}
