import java.util.ArrayList;

public class Main {
	
	// Main method, creates objects for use in the program
	public static void main(String[] args) {
		WordListGen words = new WordListGen();
		ArrayList<String> wordList = words.getWordList();
		ArrayList<String> guessableWordList = words.getGuessableWordList();
		WordleStatHandler stats = new WordleStatHandler();
		WordleStatFrame statFrame = new WordleStatFrame(stats);
		WordleGameHandler wordleGame = new WordleGameHandler(wordList, guessableWordList, stats, statFrame);
		WordFrame display = new WordFrame(wordleGame, statFrame);
	}

}
