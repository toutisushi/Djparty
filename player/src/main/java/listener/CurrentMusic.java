package listener;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import model.Song;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import process.NotificationSender;
import process.NotificationWatcher;
import process.SoundJLayer;
import dao.TableManager;

public class CurrentMusic extends JFrame implements ActionListener {

	JButton doIt, close, play, pause, open, addToPlaylist, removeFromPlaylist;
	JTable tableLibrary, tableQueue;
	JLabel label = new JLabel();
	static List<String[]> listArray1 = new ArrayList<String[]>();
	List<String[]> listArray2 = new ArrayList<String[]>();
	static SoundJLayer p;
	Object columnNames[] = { "Song Name", "Artist", "Album" };
	TableManager tableManager;

	private int pausedOnFrame = 0;

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == doIt)
			label.setText("Done!");
		else if (e.getSource() == close)
			System.exit(0);
		else if (e.getSource() == play)
			p.play((String) tableQueue.getValueAt(0, 4));
		else if (e.getSource() == pause)
			p.stop();
		else if (e.getSource() == open) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				System.out.println("Scanning dir for MP3: "
						+ chooser.getSelectedFile().getAbsolutePath());
				listArray1 = new ArrayList<String[]>();
				scanFolderForMP3(chooser.getSelectedFile());
			}
			label.setText(chooser.getSelectedFile().getAbsolutePath());
			DefaultTableModel modelPlaylist = (DefaultTableModel) tableLibrary
					.getModel();
			tableManager.clearSongTable();
			for (int i = 0; i < listArray1.size(); i++) {
				tableManager.insertSongLibrary(listArray1.get(i)[0],
						listArray1.get(i)[1], listArray1.get(i)[2],
						listArray1.get(i)[3]);
				
			}
			modelPlaylist.setRowCount(0);
			
			populateTableLibraryFromDB();
			ArrayList<Song> songs = new ArrayList<Song>();
			Song s = new Song(1, "name", "artist", "album", "path");
			Song s1 = new Song(1, "name2", "artis2t", "a2lbum", "p2ath");
			songs.add(s1);
			songs.add(s);
			
			synchronizeSongs(songs);
		} else if (e.getSource() == addToPlaylist) {
			DefaultTableModel model = (DefaultTableModel) tableQueue.getModel();
			model.addRow(new Object[] {
					tableLibrary.getValueAt(tableLibrary.getSelectedRow(), 0),
					tableLibrary.getValueAt(tableLibrary.getSelectedRow(), 1),
					tableLibrary.getValueAt(tableLibrary.getSelectedRow(), 2),
					tableLibrary.getValueAt(tableLibrary.getSelectedRow(), 3),
					tableLibrary.getValueAt(tableLibrary.getSelectedRow(), 4),1});
			

			Collections.sort(model.getDataVector(), new ColumnSorter(0));

		}

	}

	public CurrentMusic() {

		tableManager = new TableManager();
		DefaultTableModel dtm = new DefaultTableModel(0, 0);
		String headerLibrary[] = new String[] { "Id", "Song Name", "Artist",
				"Album", "Path" };
		String headerQueue[] = new String[] { "Id", "Song Name", "Artist",
				"Album", "Path", "Vote" };
		dtm.setColumnIdentifiers(headerLibrary);

		tableLibrary = new JTable(dtm);

		tableLibrary.getColumnModel().getColumn(3).setMinWidth(0);
		tableLibrary.getColumnModel().getColumn(3).setMaxWidth(0);
		tableLibrary.getTableHeader().setVisible(true);
		JScrollPane jsp = new JScrollPane(tableLibrary);
		open = new JButton("Scan Folder");

		DefaultTableModel dtm2 = new DefaultTableModel(0, 0);
		dtm2.setColumnIdentifiers(headerQueue);

		tableQueue = new JTable(dtm2);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(dtm2);
		sorter.setComparator(0, new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				return 0;
			}
		});
		tableQueue.setRowSorter(sorter);

		tableQueue.getTableHeader().setVisible(true);
		tableQueue.getColumnModel().getColumn(3).setMinWidth(0);
		tableQueue.getColumnModel().getColumn(3).setMaxWidth(0);
		// tableQueue.sorterChanged(e);
		JScrollPane jsp2 = new JScrollPane(tableQueue);

		FlowLayout cl = new FlowLayout();

		setLayout(cl);
		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();

		JPanel jp3 = new JPanel();
		JPanel jp4 = new JPanel();
		doIt = new JButton("Do It");

		doIt.setSize(20, 20);
		// add(doIt);
		play = new JButton("Play");

		play.setSize(5, 5);
		// add(play);
		pause = new JButton("Pause");
		jp1.add(doIt);
		jp1.add(play);
		jp1.add(pause);
		jp1.add(open);

		FlowLayout gl = new FlowLayout();
		jp1.setLayout(gl);
		close = new JButton("Close");
		jp1.add(close);
		jp4.add(jsp);
		jp4.add(addToPlaylist = new JButton("Add To Playlist"));

		jp4.add(jsp2);
		SongEventListener sel = new SongEventListener();
		play.addActionListener(sel);
		doIt.addActionListener(this);
		close.addActionListener(this);
		pause.addActionListener(this);
		play.addActionListener(this);
		open.addActionListener(this);
		addToPlaylist.addActionListener(this);

		jp2.add(label);
		BoxLayout glGlobal = new BoxLayout(jp3, BoxLayout.Y_AXIS);
		jp3.setLayout(glGlobal);
		jp3.add(jp1);
		jp3.add(jp4);
		jp3.add(jp2);
		add(jp3);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		TableManager.initTables();
		populateTableLibraryFromDB();
		NotificationWatcher nw = new NotificationWatcher(this);
		Thread t = new Thread(nw);
		t.start();
	}

	static private String[] getMP3FileInfos(File f) {
		FileInputStream fis = null;
		String[] result = new String[3];
		try {

			fis = new FileInputStream(f);
			InputStream input = new FileInputStream(f);
			ContentHandler handler = new DefaultHandler();
			Metadata metadata = new Metadata();
			Parser parser = new Mp3Parser();
			ParseContext parseCtx = new ParseContext();
			try {
				parser.parse(input, handler, metadata, parseCtx);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TikaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			input.close();
			fis.close();
			// List all metadata
			String[] metadataNames = metadata.names();

			for (String name : metadataNames) {
				if (name.toLowerCase().contains("album")) {
					result[2] = metadata.get(name);
				}
				if (name.toLowerCase().contains("artist")) {
					result[1] = metadata.get(name);
				}
				if (name.toLowerCase().contains("title")) {
					result[0] = metadata.get(name);
				}
				System.out.println(name + ": " + metadata.get(name));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String argv[]) {
		CurrentMusic cm = new CurrentMusic();
		p = new SoundJLayer(cm);
	}

	private String[][] addRowToTable(String songName, String Artiste,
			String album, String path) {
		String[] row = new String[4];
		row[0] = songName;

		row[1] = Artiste;
		row[2] = album;
		row[3] = path;
		listArray2.add(row);
		String[][] t = new String[3][listArray2.size()];
		for (int i = 0; i < listArray2.size(); i++) {
			t[0][i] = listArray2.get(i)[0];
			t[1][i] = listArray2.get(i)[1];
			t[2][i] = listArray2.get(i)[2];
			t[3][i] = listArray2.get(i)[2];
		}
		return t;
	}

	static void scanFolderForMP3(File dir) {
		if (dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				System.out.println(f.getName());
				if (f.getName().contains("mp3")) {
					String[] infos = getMP3FileInfos(f);
					addRowTolistLibrary(infos[0], infos[1], infos[2],
							f.getAbsolutePath(), listArray1);
				}

			}
		}

	}

	static private String[][] addRowTolistLibrary(String songName,
			String Artiste, String album, String path, List<String[]> array) {
		String[] row = new String[4];
		row[0] = songName;

		row[1] = Artiste;
		row[2] = album;
		row[3] = path;

		array.add(row);
		String[][] t = new String[4][array.size()];
		for (int i = 0; i < array.size(); i++) {
			t[0][i] = array.get(i)[0];
			t[1][i] = array.get(i)[1];
			t[2][i] = array.get(i)[2];
			t[3][i] = array.get(i)[3];
		}
		return t;
	}

	static private String[][] removeFirstRowFromList(List<String[]> array) {
		array.remove(0);
		String[][] t = new String[4][array.size()];
		for (int i = 0; i < array.size(); i++) {
			t[0][i] = array.get(i)[0];
			t[1][i] = array.get(i)[1];
			t[2][i] = array.get(i)[2];
			t[3][i] = array.get(i)[3];
		}
		return t;
	}

	public void notifySongFinished() {
		DefaultTableModel modelQueue = (DefaultTableModel) tableQueue
				.getModel();
		modelQueue.removeRow(0);
		if (modelQueue.getRowCount() != 0) {
			p.play((String) tableQueue.getValueAt(0, 3));
		}
	}

	public void notifyVote(HashMap<Integer,Integer> votes) {
		 DefaultTableModel modelQueue = (DefaultTableModel) tableQueue
		 .getModel();
		 Vector v = modelQueue.getDataVector();
		for (int i = 0; i < v.size(); i++) {
			Object[] tab = ((Vector)v.get(i)).toArray();
			int songId;
			if (tab[0] instanceof String) {
				songId = Integer.parseInt((String)tab[0]);
			}else
			{
				songId = (Integer)tab[0];
			}
			 
			if (votes.containsKey(songId)) {
				if(modelQueue.getValueAt(i, 5) == null)
				{
					modelQueue.setValueAt(1, i,5);
				}
				else
				{
					int actualVote = (Integer)modelQueue.getValueAt(i, 5) +1;
					
					modelQueue.setValueAt(actualVote++, i,5);
				}
				votes.remove(songId);
			}
			}
				
		for (Integer songId : votes.keySet()) {
			System.out.println("NEW SONG TO ADD ");
			Song s = tableManager.getSOngFromId(songId);
			modelQueue.addRow(new Object[] { s.getId(), s.getName(),
					s.getArtist(), s.getAlbum(), s.getPath(), 1 });
		}
		
		System.out.println("METHODE NOTIFY" + v);
	}

	public void populateTableLibraryFromDB() {
		ResultSet rs = tableManager.getSongLibrary();
		
		DefaultTableModel modelLibrary = (DefaultTableModel) tableLibrary
				.getModel();
		try {
			while (rs.next()) {
				modelLibrary.addRow(new Object[] { rs.getInt(1),
						rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5) });
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public void writeLibraryToDB()
	// {
	// TableManager tm =new TableManager();
	// ResultSet rs = tm.getSongLibrary();
	// DefaultTableModel modelLibrary = (DefaultTableModel) tableLibrary
	// .getModel();
	// try {
	// while (rs.next())
	// {
	// modelLibrary.addRow(new Object[] {
	// rs.getString(2),
	// rs.getString(3),
	// rs.getString(4),
	// rs.getString(5)});
	// }
	// rs.close();
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	class ColumnSorter implements Comparator {
		int colIndex;

		ColumnSorter(int colIndex) {
			this.colIndex = colIndex;
		}

		public int compare(Object a, Object b) {
			Vector v1 = (Vector) a;
			Vector v2 = (Vector) b;
			Object o1 = v1.get(colIndex);
			Object o2 = v2.get(colIndex);
			Integer val1 = null;
			Integer val2 = null;

			if (o1 == null) {
				val1 = 0;
			} else {
				if (o1 instanceof String) {
					val1 = Integer.parseInt((String) o1);
				} else {
					val1 = (Integer) o1;

				}

			}
			if (o2 == null) {
				val2 = 0;
			} else {
				if (o2 instanceof String) {
					val2 = Integer.parseInt((String) o2);
				} else {
					val2 = (Integer) o2;

				}
			}

			return val1.compareTo(val2);

		}
	}
	
	public void synchronizeSongs(List<Song> songs)
	{
		NotificationSender nw = new NotificationSender(songs);
		Thread t = new Thread(nw);
		t.start();
	}
}
