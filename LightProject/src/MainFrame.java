import javax.swing.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainFrame implements ActionListener{
	
	//Creates the frame, lights, and buttons to control the lights
	public MainFrame(int numRows, int numCols) {
		defaultFrameSetup();
		initializeFrame(numRows, numCols);
		addActionListeners();
		createButton("Randomize Board", 640, 75, 250, 50);
		createButton("Turn On/Off Row", 640, 175, 250, 50);
		createButton("Turn On/Off Column", 640, 275, 250, 50);
		createButton("Turn On/Off Grid", 640, 375, 250, 50);
		createButton("Turn On/Off All", 640, 475, 250, 50);
		Object[] choices = {"OK"};
		Object defaultChoice = choices[0];
		JOptionPane.showOptionDialog(frame, "Click any individual light to turn it on or off, or use the buttons on the right-hand side for other options.", "Information", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, defaultChoice);
		frame.setVisible(true);
	}
	
	private JFrame frame;
	private Light[][] lights;
	private ArrayList<JButton> buttons = new ArrayList<JButton>();
	
	//Creates a 960x640 frame for the lights
	public void defaultFrameSetup() {
		frame = new JFrame();
		frame.setSize(960, 640);
		frame.setResizable(false);
		frame.setTitle("Light Frame");
		frame.getContentPane().setBackground(new Color(0x4287f5));
		frame.setLayout(null);
	}
	
	//Adds ActionListeners to each light so users can click them to change them
	public void addActionListeners() {
		for(int i=0; i<lights.length; i++) {
			for(int j=0; j<lights[0].length; j++) {
				Light l = lights[i][j];
				JButton b = l.getButton();
				b.addActionListener(this);
			}
		}
	}
	
	//Creates all of the lights based on the inputted row and column numbers, adding them to the frame
	public void initializeFrame(int numRows, int numCols) {
		lights = new Light[numRows][numCols];
		int buttonSizeH = (600 - 10*(numRows + 1))/numRows;
		int buttonSizeW = (600 - 10*(numCols + 1))/numCols;
		System.out.println(buttonSizeW + " " + buttonSizeH);
		int height = 10;
		int width = 10;
		for(int i=0; i<lights.length; i++) {
			for(int j=0; j<lights[0].length; j++) {
				lights[i][j] = new Light();
				Light l = lights[i][j];
				l.getButton().setBounds(width, height, buttonSizeW, buttonSizeH);
				if(l.getOn()) {
					l.getButton().setBackground(Color.WHITE);
				}
				else {
					l.getButton().setBackground(Color.BLACK);
				}
				System.out.println("W: " + width + " H: " + height);
				width += (buttonSizeW + 10);
				frame.add(l.getButton());
			}
			height += (buttonSizeH + 10);
			width = 10;
		}
	}
	
	//Helper method used to create a button of a specific size, position, and text
	public void createButton(String txt, int x, int y, int width, int height) {
		JButton button = new JButton();
		buttons.add(button);
		button.setBounds(x, y, width, height);
		button.setForeground(Color.black);
		button.setFont(new Font("Arial", Font.PLAIN, 20));
		button.setText(txt);
		button.setOpaque(false);
		button.setBorderPainted(false);
		button.addActionListener(this);
		frame.add(button);
	}
	
	//Randomizes the statuses (on/off) of all of the lights on the board
	public void randomizeLights() {
		String cString = JOptionPane.showInputDialog(null, "What percent chance of being on do you want for each light?", "Light Randomizer", JOptionPane.QUESTION_MESSAGE);
		int chance = Integer.parseInt(cString);
		for(int i=0; i<lights.length; i++) {
			for(int j=0; j<lights[0].length; j++) {
				Light l = lights[i][j];
				int val = (int)(Math.random() * 100);
				if(val < chance) {
					l.setOn(true);
					l.getButton().setBackground(Color.WHITE);
				}
				else {
					l.setOn(false);
					l.getButton().setBackground(Color.BLACK);
				}
			}
		}
	}
	
	//Changes a row of lights to either be on or off
	//Requires the user to input the number of the desired row and whether the lights of that row are
	//to be turned on or off.
	public void turnRow() {
		String s = JOptionPane.showInputDialog(null, "Which row do you want to change (starts from 0)", "Turn On/Off Row", JOptionPane.QUESTION_MESSAGE);
		int row = Integer.parseInt(s);
		Object[] choices = {"On", "Off"};
		Object defaultChoice = choices[0];
		int b = JOptionPane.showOptionDialog(frame, "Do you want these lights to be on or off", "Turn On/Off Row", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, defaultChoice);
		if(b == 0) {
			for(int i=0; i<lights[0].length; i++) {
				lights[row][i].setOn(true);
				lights[row][i].getButton().setBackground(Color.WHITE);
			}
		}
		else {
			for(int i=0; i<lights[0].length; i++) {
				lights[row][i].setOn(false);
				lights[row][i].getButton().setBackground(Color.BLACK);
			}
		}
	}
	
	//Changes a column of lights to either be on or off
	//Requires the user to input the number of the desired column and whether the lights of that
	//column are to be turned on or off.
	public void turnColumn() {
		String s = JOptionPane.showInputDialog(null, "Which column do you want to change (starts from 0)", "Turn On/Off Column", JOptionPane.QUESTION_MESSAGE);
		int col = Integer.parseInt(s);
		Object[] choices = {"On", "Off"};
		Object defaultChoice = choices[0];
		int b = JOptionPane.showOptionDialog(frame, "Do you want these lights to be on or off", "Turn On/Off Column", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, defaultChoice);
		if(b == 0) {
			for(int i=0; i<lights.length; i++) {
				lights[i][col].setOn(true);
				lights[i][col].getButton().setBackground(Color.WHITE);
			}
		}
		else {
			for(int i=0; i<lights.length; i++) {
				lights[i][col].setOn(false);
				lights[i][col].getButton().setBackground(Color.BLACK);
			}
		}
	}
	
	//Changes a specific grid of lights to either be on or off
	//Users must specify the start and end positions, as well as whether to turn the lights on or off
	public void turnGrid() {
		String rs = JOptionPane.showInputDialog(null, "Give a starting row (minimum 0)", "Turn On/Off Grid", JOptionPane.QUESTION_MESSAGE);
		int rowS = Integer.parseInt(rs);
		String cs = JOptionPane.showInputDialog(null, "Give a starting column (minimum 0)", "Turn On/Off Grid", JOptionPane.QUESTION_MESSAGE);
		int colS = Integer.parseInt(cs);
		String re = JOptionPane.showInputDialog(null, "Give an ending row (maximum " + (lights.length - 1) + ")", "Turn On/Off Grid", JOptionPane.QUESTION_MESSAGE);
		int rowE = Integer.parseInt(re);
		String ce = JOptionPane.showInputDialog(null, "Give an ending column (maximum " + (lights[0].length - 1) + ")", "Turn On/Off Grid", JOptionPane.QUESTION_MESSAGE);
		int colE = Integer.parseInt(ce);
		Object[] choices = {"On", "Off"};
		Object defaultChoice = choices[0];
		int b = JOptionPane.showOptionDialog(frame, "Do you want these lights to be on or off", "Turn On/Off Column", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, defaultChoice);
		if(b == 0) {
			for(int i=rowS; i<=rowE; i++) {
				for(int j=colS; j<=colE; j++) {
					lights[i][j].setOn(true);
					lights[i][j].getButton().setBackground(Color.WHITE);
				}
			}
		}
		else {
			for(int i=rowS; i<=rowE; i++) {
				for(int j=colS; j<=colE; j++) {
					lights[i][j].setOn(false);
					lights[i][j].getButton().setBackground(Color.BLACK);
				}
			}
		}
	}
	
	//Turns all of the lights on or off for the entire grid
	public void turnAll() {
		Object[] choices = {"On", "Off"};
		Object defaultChoice = choices[0];
		int b = JOptionPane.showOptionDialog(frame, "Do you want these lights to be on or off", "Turn On/Off Column", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, defaultChoice);
		for(int i=0; i<lights.length; i++) {
			for(int j=0; j<lights[0].length; j++) {
				if(b == 0) {
					lights[i][j].setOn(true);
					lights[i][j].getButton().setBackground(Color.WHITE);
				}
				else{
					lights[i][j].setOn(false);
					lights[i][j].getButton().setBackground(Color.BLACK);
				}
			}
		}
	}
	
	//Performs the actions of buttons when clicked
	@Override
	public void actionPerformed(ActionEvent e) {
		for(int i=0; i<buttons.size(); i++) {
			if(e.getSource() == buttons.get(i)) {
				if(i == 0) {
					randomizeLights();
					return;
				}
				else if(i == 1) {
					turnRow();
					return;
				}
				else if(i == 2) {
					turnColumn();
					return;
				}
				else if(i == 3) {
					turnGrid();
					return;
				}
				else if(i == 4) {
					turnAll();
					return;
				}
			}
		}
		for(int i=0; i<lights.length; i++) {
			for(int j=0; j<lights[0].length; j++) {
				Light l = lights[i][j];
				if(e.getSource() == l.getButton()) {
					if(l.getOn() == false) {
						l.setOn(true);
						l.getButton().setBackground(Color.WHITE);
					}
					else {
						l.setOn(false);
						l.getButton().setBackground(Color.BLACK);
					}
					return;
				}
			}
		}
		
	}
}
