package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Song;

public class TableManager {

	static public void initTables() {
		ResultSet rs = null;
		Connection connection = getConnection();
		try {
//			 connection.prepareStatement("drop table songs if exists;").execute();
			connection
					.prepareStatement(
							"create table if not exists songs(id integer primary key identity, name varchar(60),artist varchar(60),album varchar(60),path varchar(60));")
					.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// try {
		// connection.prepareStatement("insert into songs(name,artist,album,path)"
		// +
		// " values ('love me tender','Elvis','Best Of','C://path');").execute();
		// connection.prepareStatement("insert into songs(name,artist,album,path)"
		// +
		// " values ('love me tender','Elvis','Best Of','C://path');").execute();
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		try {
			rs = connection.prepareStatement("select id, name from songs;")
					.executeQuery();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			rs.next();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			System.out.println(String.format("ID:%1d,Name : 1%s", rs.getInt(1),
					rs.getString(2)));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static Connection getConnection() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(
					"jdbc:hsqldb:file:djparty", "dj", "");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return connection;
	}

	public ResultSet getSongLibrary() {
		Connection connection = getConnection();
		ResultSet rs = null;

		try {
			rs = connection.prepareStatement(
					"select id, name,artist,album,path from songs;")
					.executeQuery();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return rs;
	}

	public void insertSongLibrary(String name, String artist, String album,
			String path) {
		Connection connection = getConnection();

		try {
			connection.prepareStatement(
					"insert into songs(name,artist,album,path)" + " values ('"
							+ name + "','" + artist + "','" + album + "','"
							+ path + "');").execute();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public Song getSOngFromId(Integer songId) {
		
		Connection connection = getConnection();
		ResultSet rs = null;

		try {
			rs = connection.prepareStatement(
					"select id, name,artist,album,path from songs where id="
							+ songId + ";").executeQuery();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			rs.next();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Song res = null;
		try {
			res = new Song(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
				
		return res;
	}
	
	public void clearSongTable() {
		
		Connection connection = getConnection();

		try {
			connection.prepareStatement(
					"truncate table songs;").execute();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
}