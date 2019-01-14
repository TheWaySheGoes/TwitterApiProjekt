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

import main.RiksdagModule.DataType;

public class ModuleGui implements ListSelectionListener, ActionListener, Runnable{
	private JFrame frame; // The Main window
	private JTextField txtInput;
	private JButton btnStartRik;
	private JButton btnStopRik;
	private JButton btnSetIntervalRik;
	private JButton btnExitRik;
	private JTextArea txtOutput;
	private JScrollPane txtScroll;
	private Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
	private JList<String> tableList;
	private RiksdagModule riksdagModule;
	private TwitterModule twitterModule;
	
	
	//master branch test 1
	//master after branching 1
	//master after importing branch 1

	private JScrollPane tableListScroll;
	private JTextArea info;

	
	private void initializeGUI() {
		riksdagModule=new RiksdagModule(DataType.JUMBO,this,1);
		

		
		info =new JTextArea();
		info.setEditable(true);
		info.setBounds(20, 20, 760, 130);
		frame.add(info);
		
		this.txtInput = new JTextField("");
		this.txtInput.setBounds(20, 150, 760, 30);
		this.txtInput.setBorder(border);
		frame.add(txtInput);

		this.btnStartRik = new JButton("START");
		this.btnStartRik.setBounds(150, 180, 100, 20);
		this.btnStartRik.addActionListener(this);
		frame.add(btnStartRik);
		this.btnStopRik = new JButton("STOP");
		this.btnStopRik.setBounds(270, 180, 100, 20);
		this.btnStopRik.addActionListener(this);
		frame.add(btnStopRik);
		this.btnSetIntervalRik = new JButton("SET INTERVAL");
		this.btnSetIntervalRik.setBounds(380, 180, 100, 20);
		this.btnSetIntervalRik.addActionListener(this);
		frame.add(btnSetIntervalRik);
		this.btnExitRik = new JButton("EXIT");
		this.btnExitRik.setBounds(490, 180, 100, 20);
		this.btnExitRik.addActionListener(this);
		frame.add(btnExitRik);

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

		info.setText("\nSTART - startar Module for getting data from Riksdag and Twitter APIs\n"
				+ "STOP - soft stop after running one time\n"
				+ "EXIT - Hard stop \n"
				+ "SET INTERVAL - Sets how often data gets updated \n" );
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
		
		
	
}
	
		
	public void displayTxt(String txt) {
		txtOutput.setText(txtOutput.getText()+"\n"+txt+"\n");
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.btnSetIntervalRik) {
			riksdagModule.setTimeInterval(Integer.parseInt(txtInput.getText()));
			twitterModule.setTimeInterval(Integer.parseInt(txtInput.getText()));
		}
		
		if (e.getSource() == this.btnStartRik) {
			riksdagModule.start();
			twitterModule.start();
		}
		
		if (e.getSource() == this.btnExitRik) { 
			riksdagModule.exit();
			riksdagModule.exit();
		}
		
		
		if (e.getSource() == this.btnStopRik) {
			riksdagModule.stop();
			twitterModule.stop();
		}
	
	}

	public static void main(String[] args) {
		ModuleGui rmg = new ModuleGui();
		SwingUtilities.invokeLater(rmg);
	}

	@Override
	public void valueChanged(ListSelectionEvent l) {

		if (!l.getValueIsAdjusting()) {
			
		}

	}
	}

