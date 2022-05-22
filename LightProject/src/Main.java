import javax.swing.JOptionPane;

public class Main {
	
	//Creates a JOptionPane allowing users to choose the size of the light board
	//(1-10 rows) by (1-10 columns)
	//After these choices are made, a lightboard is created
	public static void main(String[] args) {
		Object[] choices = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
		Object defaultChoice = choices[0];
		String rowString = (String) JOptionPane.showInputDialog(null, "How many rows of lights?", "Set Rows", JOptionPane.QUESTION_MESSAGE, null, choices, defaultChoice);
		String colString = (String) JOptionPane.showInputDialog(null, "How many columns of lights?", "Set Columns", JOptionPane.QUESTION_MESSAGE, null, choices, defaultChoice);
		int rows = Integer.parseInt(rowString);
		int cols = Integer.parseInt(colString);
		MainFrame main = new MainFrame(rows, cols);
	}

}