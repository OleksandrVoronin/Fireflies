/**
 * Execution class to run an applet as a desktop app, can be exported as a runnable jar file
 * */

package fireflies.the.game;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class Exec extends JFrame {
	
	private static final long serialVersionUID = 7755511300672834448L;

	public Exec() {
		this.setLayout(new BorderLayout());
		this.add(new MainClass(), BorderLayout.CENTER);
		this.setSize(MainClass.SCREEN_SIZE_X + 6, MainClass.SCREEN_SIZE_Y + 28);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new Exec();
	}

}
