
public class WordleOutcome {
	
	public boolean isWinner;
	public int numGuesses;
	
	//Creates an object with a boolean determining if the player won or loss, and the number of guesses used
	public WordleOutcome(boolean win, int guesses) {
		isWinner = win;
		numGuesses = guesses;
	}
	
	//Returns isWinner variable
	public boolean getWin() {return isWinner;}
	//Returns numGuesses variable
	public int getNumGuesses() {return numGuesses;}
}
