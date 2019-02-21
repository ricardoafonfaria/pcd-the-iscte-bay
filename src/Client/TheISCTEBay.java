package Client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import Messages.FileDetails;

public class TheISCTEBay implements Observer {

	private static final long BLOCK_SIZE = 1024;
	private DefaultListModel<FileDetails> list = new DefaultListModel<FileDetails>();
	private JList<FileDetails> jlist = new JList<FileDetails>(list);
	private JFrame frame;
	private Client client;
	private JProgressBar bar;

	public TheISCTEBay(String[] args) {
		client = new Client(args);
		client.addObserver(this);
		frame = new JFrame("The ISCTE Bay");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		bar = new JProgressBar();
		JLabel search_label = new JLabel("Keyword:");
		JTextField search_box = new JTextField("");
		JButton search_button = new JButton("Search");
		search_label.setHorizontalAlignment(SwingConstants.LEFT);
		search_box.setPreferredSize(new Dimension(150, 20));
		search_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				list.clear();
				client.search(search_box.getText());
				list.addAll(client.getMap().keySet());
			}
		});

		JPanel upper = new JPanel();
		upper.setLayout(new FlowLayout());
		upper.add(search_label);
		upper.add(search_box);
		upper.add(search_button);
		frame.add(upper, BorderLayout.NORTH);

		jlist.setPreferredSize(new Dimension(200, 200));
		frame.add(new JScrollPane(jlist), BorderLayout.CENTER);
		JButton download = new JButton("Download");
		download.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				download.setEnabled(false);
				FileDetails file = jlist.getSelectedValue();
				int max = file.getSize() % BLOCK_SIZE == 0 ? (int) (file.getSize() / BLOCK_SIZE)
						: (int) (file.getSize() / BLOCK_SIZE) + 1;
				bar.setMinimum(0);
				bar.setMaximum(max);
				bar.setValue(0);
				new Download(client, file, frame, download).execute();
			}
		});
		JPanel bottom = new JPanel();
		bottom.setLayout(new GridLayout(1, 2));
		bottom.add(bar);
		bottom.add(download);
		frame.add(bottom, BorderLayout.SOUTH);
		frame.pack();
	}

	private void init() {
		frame.setVisible(true);
	}

	@Override
	public void update(Observable o, Object arg) {
		bar.setValue(client.getProgress());
	}

	public static void main(String[] args) {
		TheISCTEBay bay = new TheISCTEBay(args);
		bay.init();
	}

}