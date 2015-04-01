package model;

public class Song {
	
	int id;
	String name;
	String artist;
	String album;
	String path;

	
	
	public Song(int id, String name, String artist, String album, String path) {
		super();
		this.id = id;
		this.name = name;
		this.artist = artist;
		this.album = album;
		this.path = path;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}

}
