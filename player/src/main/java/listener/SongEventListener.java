package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SongEventListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Called SongEventListener");

	}

}
