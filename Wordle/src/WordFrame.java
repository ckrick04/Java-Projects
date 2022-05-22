import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

public class WordFrame implements ActionListener{
	
	private JFrame frame;
	private JPanel[][] letters;
	private JLabel[][] letters2;
	private JTextField textBox;
	private JButton textBoxEnter;
	private JButton statsButton;
	private JButton replay;
	private JLabel wordleLabel;
	private JLabel messageLabel;
	private WordleGameHandler wordleGame;
	private WordleStatFrame statFrame;
	
	//Constructor for the display of the frame, creates the frame and its elements
	public WordFrame(WordleGameHandler wordleGame, WordleStatFrame sf) {
		this.wordleGame = wordleGame;
		statFrame = sf;
		initializeFrame();
		initializeButtons();
		initializeTextAreas();
		frame.setVisible(true);
	}
	
	//Creates a frame with a set size and other options
	public void initializeFrame() {
		frame = new JFrame();
		frame.setSize(400, 700);
		frame.setTitle("Wordle");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLayout(null);
	}
	
	//Creates all of the buttons that will be displayed on the frame previously created
	public void initializeButtons() {
		letters = new JPanel[6][5];
		letters2 = new JLabel[6][5];
		int x = 20;
		int y = 100;
		for(int i=0; i<letters.length; i++) {
			for(int j=0; j<letters[0].length; j++) {
				JPanel c = new JPanel();
				JLabel l = new JLabel();
				letters[i][j] = c;
				letters2[i][j] = l;
				c.setBounds(x, y, 60, 60);
				c.setBackground(Color.white);
				c.add(l);
				l.setBounds(x, y, 60, 60);
				l.setFont(new Font("Arial", Font.BOLD, 36));
				l.setForeground(Color.white);
				frame.add(c);
				x += 70;
			}
			y += 70;
			x = 20;
		}
		textBoxEnter = new JButton();
		textBoxEnter.setBounds(260, 540, 100, 30);
		textBoxEnter.setText("Enter");
		textBoxEnter.addActionListener(this);
		frame.add(textBoxEnter);
		
		statsButton = new JButton();
		statsButton.setBounds(20, 585, 150, 30);
		statsButton.setText("Your Stats");
		statsButton.addActionListener(this);
		frame.add(statsButton);
		
		replay = new JButton();
		replay.setBounds(210, 585, 150, 30);
		replay.setText("Replay");
		replay.addActionListener(this);
		frame.add(replay);
	}
	
	//Creates the text areas that will be displayed on the frame
	public void initializeTextAreas() {
		textBox = new JTextField();
		textBox.setBounds(20, 540, 220, 30);
		textBox.setFont(new Font("Arial", Font.PLAIN, 20));
		textBox.setEditable(true);
		frame.add(textBox);
		
		wordleLabel = new JLabel();
		wordleLabel.setBounds(20, 10, 340, 50);
		wordleLabel.setFont(new Font("Arial", Font.BOLD, 40));
		wordleLabel.setHorizontalAlignment(JLabel.CENTER);
		wordleLabel.setVerticalAlignment(JLabel.CENTER);
		wordleLabel.setText("WORDLE");
		frame.add(wordleLabel);
		
		messageLabel = new JLabel();
		messageLabel.setBounds(20, 70, 340, 30);
		messageLabel.setForeground(Color.red);
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		messageLabel.setVerticalAlignment(JLabel.CENTER);
		messageLabel.setFont(new Font("Arial", Font.BOLD, 24));
		frame.add(messageLabel);
	}
	
	//Gets if a guess is correct or not using the WordleGameHandler's guess method
	//If the guess is 5 letters and in the wordbank, a row of squares show the word guessed with colored background
	//Green is shown if the letter guessed is correct for the given position
	//Yellow is shown if the letter guessed is not correct for the given position, however it still appears elsewhere in the word
	//Gray is shown if the letter guessed is neither correct for the given position nor appears elsewhere in the word
	public void guess(String origGuess){
		String guess = origGuess.toUpperCase();
		String guessCorrectness = wordleGame.guess(guess);
		if(guessCorrectness != null) {
			for(int i=0; i<guessCorrectness.length(); i++) {
				JPanel b = letters[wordleGame.getCurrentGuess() - 1][i];
				JLabel l = letters2[wordleGame.getCurrentGuess() - 1][i];
				String g = guessCorrectness.substring(i, i+1);
				String h = guess.substring(i, i+1);
				if(g.equals("*")) {
					b.setBackground(new Color(0xffd800));
				}
				else if(g.equals("+")) {
					b.setBackground(Color.gray);
				}
				else {
					b.setBackground(Color.green);
				}
				l.setText(h);
			}
			messageLabel.setText("");
		}
		else {
			messageLabel.setForeground(Color.red);
			if(guess.length() != 5) {
				messageLabel.setText("Guess Is Not 5 Letters");
			}
			else{
				messageLabel.setText("Not In Word List");
			}
		}
		setMessageLabel();
	}
	
	//When all guesses are complete, shows if the user won or lost, showing the correct word if they lost
	public void setMessageLabel() {
		if(wordleGame.getWinCondition().equals("W")) {
			messageLabel.setForeground(Color.green);
			messageLabel.setText("You won!");
		}
		else if(wordleGame.getWinCondition().equals("L")) {
			messageLabel.setForeground(Color.red);
			messageLabel.setText("You lost! (" + wordleGame.getWord() + ")" );
		}
	}
	
	//Clears the screen in order to begin a new wordle game
	public void clearScreen() {
		for(int i=0; i<letters.length; i++) {
			for(int j=0; j<letters[0].length; j++) {
				JPanel p = letters[i][j];
				JLabel l = letters2[i][j];
				p.setBackground(Color.white);
				l.setText("");
			}
		}
		messageLabel.setText("");
		textBox.setText("");
		if(statFrame != null && statFrame.getFrame().isVisible()) {
			statFrame.getFrame().setVisible(false);
		}
	}
	
	//Returns the WordleGameHandler object
	public WordleGameHandler getGameManager() {return wordleGame;}
	
	//actionPerformed method used to interact with buttons 
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == textBoxEnter) {
			if(wordleGame.getWinCondition().equals("I")) {
				guess(textBox.getText());
				if(!wordleGame.getWinCondition().equals("I")) {
					if(statFrame != null && statFrame.getFrame().isVisible()) {
						statFrame.getFrame().setVisible(false);
					}
				}
			}
		}
		else if(e.getSource() == statsButton) {
			if(statFrame != null && statFrame.getFrame().isVisible()) {
				statFrame.getFrame().setVisible(false);
			}
			statFrame = new WordleStatFrame(wordleGame.getStatHandler());
			statFrame.getFrame().setVisible(true);
		}
		else if(e.getSource() == replay) {
			clearScreen();
			wordleGame.resetGame();
		}
		
	}
}
