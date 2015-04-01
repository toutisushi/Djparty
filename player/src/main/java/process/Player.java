package process;

import java.awt.EventQueue;

import listener.CurrentMusic;

public class Player {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				CurrentMusic cm = new CurrentMusic();
				cm.setVisible(true);
			}

		});
	}
}