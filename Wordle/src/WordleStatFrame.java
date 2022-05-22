import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WordleStatFrame {
	
	private JFrame frame;
	private JLabel winRate;
	private JLabel totalPlayed;
	private JLabel winTotal;
	private JLabel guessDistribution;
	private JPanel[] guessBars;
	private JLabel[] numberLabels;
	private JLabel[] percentLabels;
	private WordleStatHandler statHandler;
	
	//Creates the stats frame and its components
	public WordleStatFrame(WordleStatHandler s) {
		statHandler = s;
		initializeFrame();
		initalizeText();
		initializeBars();
		initializeNumberLabels();
		initializeGuessWinLabels();
		updateBars();
		frame.setVisible(false);
	}
	
	//Creates the stat frame, set at 300 x 500 resolution
	public void initializeFrame() {
		frame = new JFrame();
		frame.setSize(300, 500);
		frame.setTitle("Your Stats");
		frame.setResizable(false);
		frame.setLayout(null);
	}
	
	//Creates all of the labels and text that will be on the frame, including the number of games played, games won, and winrate
	public void initalizeText() {
		winTotal = new JLabel();
		winTotal.setBounds(10, 10, 280, 25);
		winTotal.setFont(new Font("Arial", Font.PLAIN, 18));
		winTotal.setText("Games Won: " + statHandler.getNumWins());
		frame.add(winTotal);
		
		totalPlayed = new JLabel();
		totalPlayed.setBounds(10, 35, 280, 25);
		totalPlayed.setFont(new Font("Arial", Font.PLAIN, 18));
		totalPlayed.setText("Games Played: " + statHandler.getTotalGames());
		frame.add(totalPlayed);
		
		winRate = new JLabel();
		winRate.setBounds(10, 60, 280, 25);
		winRate.setFont(new Font("Arial", Font.PLAIN, 18));
		if(statHandler.getTotalGames() > 0) {
			winRate.setText("Win Percentage: " + statHandler.round(100.0*statHandler.getNumWins()/statHandler.getTotalGames()) + "%");
		}
		else {
			winRate.setText("Win Percentage: 0%");
		}
		frame.add(winRate);
		
		guessDistribution = new JLabel();
		guessDistribution.setBounds(10, 100, 270, 18);
		guessDistribution.setFont(new Font("Arial", Font.PLAIN, 18));
		guessDistribution.setHorizontalAlignment(JLabel.CENTER);
		guessDistribution.setText("Guess Distribution");
		frame.add(guessDistribution);
	}
	
	//Creates the bars which show the distribution of guesses it took to win games
	public void initializeBars() {
		guessBars = new JPanel[6];
		int x = 12;
		int y = 330;
		for(int i=0; i<guessBars.length; i++) {
			guessBars[i] = new JPanel();
			JPanel c = guessBars[i];
			c.setBackground(Color.green);
			c.setBounds(x, y, 36, 0);
			frame.add(c);
			x += 44;
		}
	}
	
	//Creates number labels from 1 to 6 for the guess distribution portion of the frame
	public void initializeNumberLabels() {
		numberLabels = new JLabel[6];
		int x = 12;
		for(int i=0; i<numberLabels.length; i++){
			numberLabels[i] = new JLabel();
			JLabel c = numberLabels[i];
			c.setFont(new Font("Arial", Font.BOLD, 28));
			c.setVerticalAlignment(JLabel.CENTER);
			c.setHorizontalAlignment(JLabel.CENTER);
			c.setText("" + (i+1));
			c.setBounds(x, 330, 36, 36);
			frame.add(c);
			x += 44;
		}
	}
	
	//Creates labels to display numbers of wins by guess count for the guess distribution portion of the frame
	public void initializeGuessWinLabels() {
		percentLabels = new JLabel[6];
		int x = 12;
		for(int i=0; i<percentLabels.length; i++){
			numberLabels[i] = new JLabel();
			JLabel c = numberLabels[i];
			c.setFont(new Font("Arial", Font.BOLD, 14));
			c.setVerticalAlignment(JLabel.CENTER);
			c.setHorizontalAlignment(JLabel.CENTER);
			c.setText("(" + statHandler.getNumWinsByGuess()[i] + ")");
			c.setBounds(x, 366, 36, 36);
			frame.add(c);
			x += 44;
		}
	}
	
	//Recreates the bars in the guess distribution each time a game is played
	public void updateBars() {
		int totalWins = statHandler.getNumWins();
		int size1 = 0;
		int size2 = 0;
		int size3 = 0;
		int size4 = 0;
		int size5 = 0;
		int size6 = 0;
		if(totalWins > 0) {
			size1 = (int)(200*statHandler.getNumWinsByGuess()[0]/totalWins);
			size2 = (int)(200*statHandler.getNumWinsByGuess()[1]/totalWins);
			size3 = (int)(200*statHandler.getNumWinsByGuess()[2]/totalWins);
			size4 = (int)(200*statHandler.getNumWinsByGuess()[3]/totalWins);
			size5 = (int)(200*statHandler.getNumWinsByGuess()[4]/totalWins);
			size6 = (int)(200*statHandler.getNumWinsByGuess()[5]/totalWins);
		}
		int[] sizes = {size1, size2, size3, size4, size5, size6};
		int[] y = {330-size1, 330-size2, 330-size3, 330-size4, 330-size5, 330-size6};
		int x = 12;
		for(int i=0; i<guessBars.length; i++) {
			JPanel c = guessBars[i];
			c.setBounds(x, y[i], 36, sizes[i]);
			x += 44;
		}	
	}
	
	//Returns the frame
	public JFrame getFrame() {return frame;}
}
