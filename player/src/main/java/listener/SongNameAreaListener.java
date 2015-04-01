package listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import event.ChangeNameEvent;

public class SongNameAreaListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		ChangeNameEvent cne = new ChangeNameEvent(arg0, 1, 1);
		cne.notify();

	}
	
	public void actionPerformed(ChangeNameEvent cne)
	{
		System.out.println("actionPerformed ChangeName");
	}

}
