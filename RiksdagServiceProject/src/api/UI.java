package api;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * UI for the API. Is used to start the API. 
 * 
 * @author André Hansson
 */
public class UI implements ActionListener {

	private Api api;

	private JFrame frame;
	private JPanel mainPanel;
	private JPanel gridPanel;
	private JLabel infoLbl;
	private JLabel statusLbl;
	private JPanel btnPanel;
	private JButton startBtn;
	private JButton stopBtn;
	private JPanel logPanel;
	private JTextArea logTextArea;
	private JScrollPane logScrollPane;

	public UI() {

		api = new Api(this);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			api.stop();
		}));

		SwingUtilities.invokeLater(() -> {

			frame = new JFrame("Politiknörden");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			mainPanel = new JPanel();
			mainPanel.setPreferredSize(new Dimension(400, 600));

			gridPanel = new JPanel(new GridLayout(4, 1));
			gridPanel.setPreferredSize(new Dimension(400, 200));

			infoLbl = new JLabel("localhost:5000");
			infoLbl.setHorizontalAlignment(SwingConstants.CENTER);

			statusLbl = new JLabel("Status: Not running");
			statusLbl.setHorizontalAlignment(SwingConstants.CENTER);

			btnPanel = new JPanel(new GridLayout(1, 2));

			startBtn = new JButton("Start");
			startBtn.addActionListener(this);
			btnPanel.add(startBtn);

			stopBtn = new JButton("Stop");
			stopBtn.addActionListener(this);
			stopBtn.setEnabled(false);
			btnPanel.add(stopBtn);

			logPanel = new JPanel();
			logPanel.setPreferredSize(new Dimension(400, 400));

			logTextArea = new JTextArea("API log" + System.lineSeparator());
			logTextArea.setLineWrap(true);
			logTextArea.setDisabledTextColor(Color.BLACK);
			logTextArea.setEnabled(false);

			logScrollPane = new JScrollPane(logTextArea);
			logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			logScrollPane.setPreferredSize(new Dimension(380, 380));

			gridPanel.add(infoLbl);
			gridPanel.add(statusLbl);
			gridPanel.add(btnPanel);

			logPanel.add(logScrollPane);

			mainPanel.add(gridPanel);
			mainPanel.add(logPanel);

			frame.setContentPane(mainPanel);
			frame.pack();
			frame.setVisible(true);
			
		});
	}

	public static void main(String[] args) {
		new UI();
	}

	public void log(String str) {
		logTextArea.append(str + System.lineSeparator());
		logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startBtn) {
			api.start();
			statusLbl.setText("Status: Running");
			log("Spark started");
			stopBtn.setEnabled(true);
			startBtn.setEnabled(false);
		} else if (e.getSource() == stopBtn) {
			api.stop();
			statusLbl.setText("Status: Stopped");
			log("Spark stopped");
			startBtn.setEnabled(true);
			stopBtn.setEnabled(false);
		}
	}

}