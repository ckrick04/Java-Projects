import java.util.ArrayList;

public class WordleGameHandler {
	
	private String wordToFind;
	private ArrayList<String> words;
	private ArrayList<String> guessableWords;
	private String winCondition;
	private int currentGuess;
	private WordleStatHandler statHandler;
	private WordleStatFrame openFrame;
	
	//Sets arraylists of words to those provided in the main method. Creates WordleStatHandler and WordleStatFrame objects to track player stats
	//Sets the win condition to "I", signifying that a game is in progress, and currentGuess to 0, signifying that a user is on their first guess
	public WordleGameHandler(ArrayList<String> words, ArrayList<String> guessableWords, WordleStatHandler s, WordleStatFrame sf) {
		this.words = words;
		this.guessableWords = guessableWords;
		this.openFrame = sf;
		winCondition = "I";
		currentGuess = 0;
		wordToFind = guessableWords.get((int)(Math.random() * guessableWords.size()));
		statHandler = s;
		System.out.println(wordToFind);
	}
	
	//Returns a string showing the correctness of a player guess
	//A letter signifies that the specified letter is in the correct position
	//A "*" signifies that the letter is not in the correct position but is somewhere else in the word
	//A "+" signifies that the letter is not correct nor anywhere in the word
	public String guess(String guess) {
		String ret = "";
		if(isPossible(guess)) {
			for(int i=0; i<wordToFind.length(); i++) {
				String gletter = guess.substring(i, i+1);
				String cletter = wordToFind.substring(i, i+1);
				if(wordToFind.indexOf(gletter) > -1) {
					if(gletter.equals(cletter)) {
						ret += gletter;					
					}
					else {
						ret += "*";
					}
				}
				else {
					ret += "+";
				}
			}
			currentGuess++;
			winCondition = isWinner(guess);
			return ret;
		}
		return null;
	}
	
	//Determines if a guess is possible 
	//The guess must be 5 letters and in the wordlist to be possible
	public boolean isPossible(String guess) {
		if(guess.length() != 5) {
			return false;
		}
		for(int i=0; i<words.size(); i++) {
			if(guess.equals(words.get(i))) {
				return true;
			}
		}
		return false;
	}
	
	//After a guess is made, determines if a player won, lost, or is still in progress
	public String isWinner(String guess) {
		if(guess.equals(wordToFind)) {
			WordleOutcome o = new WordleOutcome(true, currentGuess);
			statHandler.addStat(o);
			if(openFrame != null && openFrame.getFrame().isVisible()) {
				openFrame.getFrame().setVisible(false);
			}
			openFrame = new WordleStatFrame(statHandler);
			openFrame.getFrame().setVisible(true);
			return "W";
		}
		else if(currentGuess >= 6) {
			WordleOutcome o = new WordleOutcome(false, currentGuess);
			statHandler.addStat(o);
			if(openFrame != null && openFrame.getFrame().isVisible()) {
				openFrame.getFrame().setVisible(false);
			}
			openFrame = new WordleStatFrame(statHandler);
			openFrame.getFrame().setVisible(true);
			return "L";
		}
		return "I";
	}
	
	//Sets the game back to its original settings
	public void resetGame() {
		winCondition = "I";
		currentGuess = 0;
		wordToFind = guessableWords.get((int)(Math.random() * guessableWords.size()));
		if(openFrame != null) {
			openFrame.getFrame().setVisible(false);
		}
		System.out.println(wordToFind);
	}
	
	//Returns the word the user is supposed to find
	public String getWord() {return wordToFind;}
	//Returns the status of the game (won, lost, in progress)
	public String getWinCondition() {return winCondition;}
	//Returns the numerical guess that the user is currently on
	public int getCurrentGuess() {return currentGuess;}
	//Returns the stat handler object
	public WordleStatHandler getStatHandler() {return statHandler;}
}

