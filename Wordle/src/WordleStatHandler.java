import java.util.ArrayList;

public class WordleStatHandler {
	
	public ArrayList<WordleOutcome> wordleStats;
	public int numWins;
	public int numLosses;
	public int totalGamesPlayed;
	public int[] numWinsByGuess;
	
	//Initializes values when the Wordle program is opened
	public WordleStatHandler() {
		wordleStats = new ArrayList<WordleOutcome>();
		numWins = 0;
		numLosses = 0;
		totalGamesPlayed = 0;
		numWinsByGuess = new int[6];
		for(int i=0; i<numWinsByGuess.length; i++) {
			numWinsByGuess[i] = 0;
		}
	}
	
	//Modifies the instance variables at the end of the game
	//Values for number of wins, losses, games played, and wins by guess are changed based on the outcome
	public void addStat(WordleOutcome e) {
		if(e.getWin()) {
			numWins++;
			numWinsByGuess[e.getNumGuesses() - 1]++;
		}
		else {
			numLosses++;
		}
		totalGamesPlayed++;
		wordleStats.add(e);
	}
	
	//Rounds up or down to the nearest whole number based on conventional standards
	public int round(double d) {
		int ret = (int)d;
		if(d - ret >= 0.5) {
			ret++;
		}
		return ret;
	}
	
	//Returns number of wins
	public int getNumWins() {return numWins;}
	//Returns number of losses
	public int getNumLosses() {return numLosses;}
	//Returns an array of number of wins for each possible number of guesses (1-6)
	public int[] getNumWinsByGuess() {return numWinsByGuess;}
	//Returns number of games played
	public int getTotalGames() {return totalGamesPlayed;}
}
