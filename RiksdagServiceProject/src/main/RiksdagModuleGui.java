package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;

import main.Main.DataType;

public class RiksdagModuleGui implements ListSelectionListener, ActionListener, Runnable{
	private JFrame frame; // The Main window
	private JTextField txtInput;
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnSetInterval;
	private JButton btnExit;
	private JTextArea txtOutput;
	private JScrollPane txtScroll;
	private Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
	private JList<String> tableList;
	private Main main;
	
	
	//master branch test 1
	//master after branching 1
	//master after importing branch 1

	private JScrollPane tableListScroll;
	private JTextArea info;

	
	private void initializeGUI() {
		

		tableList = new JList<String>();
		tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableList.addListSelectionListener(this);
		tableListScroll = new JScrollPane(tableList);
		tableListScroll.setBounds(20, 20, 100, 130);
		frame.add(tableListScroll);
		
		info =new JTextArea();
		info.setEditable(false);
		info.setBounds(140, 20, 600, 130);
		frame.add(info);
		
		this.txtInput = new JTextField("");
		this.txtInput.setBounds(20, 150, 760, 30);
		this.txtInput.setBorder(border);
		frame.add(txtInput);

		this.btnStart = new JButton("START");
		this.btnStart.setBounds(150, 180, 100, 20);
		this.btnStart.addActionListener(this);
		frame.add(btnStart);
		this.btnStop = new JButton("STOP");
		this.btnStop.setBounds(250, 180, 100, 20);
		this.btnStop.addActionListener(this);
		frame.add(btnStop);
		this.btnSetInterval = new JButton("SET INTERVAL");
		this.btnSetInterval.setBounds(350, 180, 100, 20);
		this.btnSetInterval.addActionListener(this);
		frame.add(btnSetInterval);
		this.btnExit = new JButton("EXIT");
		this.btnExit.setBounds(450, 180, 100, 20);
		this.btnExit.addActionListener(this);
		frame.add(btnExit);

		this.txtOutput = new JTextArea("*******----JAVA RIKSDAG MODULE----*******");
		this.txtOutput.setTabSize(0);
		this.txtOutput.setFont(new Font("monospaced", Font.PLAIN, 12));
		this.txtOutput.setDisabledTextColor(Color.BLACK);
		this.txtOutput.setEnabled(false);
		DefaultCaret caret = (DefaultCaret)txtOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.txtScroll = new JScrollPane(this.txtOutput);
		this.txtScroll.setViewportView(txtOutput);
		this.txtScroll.setBounds(20, 200, 760, 300);
		this.txtScroll.setBorder(border);
		frame.add(txtScroll);

		info.setText("iNFO BOX ......");
	}

	@Override
	public void run() {
		frame = new JFrame();
		frame.setBounds(0, 0, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setTitle("psql service GUI");
		initializeGUI();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main=new Main(DataType.JUMBO,this);
		
	
}

		
	public void displayTxt(String txt) {
		txtOutput.setText(txtOutput.getText()+"\n"+txt);
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.btnSetInterval) {
			main.setInterval(Integer.parseInt(txtInput.getText()));

		}
		
		if (e.getSource() == this.btnStart) {
			main.start();
			
		}
		
		if (e.getSource() == this.btnExit) {
			main.exit();
		}
		
		
		if (e.getSource() == this.btnStop) {
			main.stop();
		}
	
	}

	public static void main(String[] args) {
		RiksdagModuleGui rmg = new RiksdagModuleGui();
		SwingUtilities.invokeLater(rmg);
	}

	@Override
	public void valueChanged(ListSelectionEvent l) {

		if (!l.getValueIsAdjusting()) {
			
		}

	}
	}

