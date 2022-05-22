import javax.swing.JButton;

public class Light {
	
	private JButton button;
	private boolean isOn;
	
	//Creates a light with a button and a boolean
	//The boolean is the status of the light, representing whether its on or off
	//The button is colored black or white and can be used to change the status of the light
	public Light() {
		button = new JButton();
		int val = (int)(Math.random() * 10);
		if(val < 4) {
			isOn = true;
		}
		else {
			isOn = false;
		}
	}
	
	//Returns the boolean isOn
	public boolean getOn() {return isOn;}
	//Returns the light's button
	public JButton getButton() {return button;}
	
	//Sets the status of the light
	public void setOn(boolean b) {isOn = b;}
	//Sets the light's JButton
	public void setButton(JButton b) {button = b;}
}
