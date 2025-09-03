package utils;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
	
	private static String absolutePathDB = Paths.get("").toAbsolutePath()
		    .resolve("database")
		    .resolve("coffeMachine.db")
		    .toString();

	static private final String pathDB = "jdbc:sqlite:" + absolutePathDB;
	static private DB database = null;

	private DB() {
		database = this;
	}

	public static DB getInstance() {
		if (database == null)
			return new DB();
		else {
			return database;
		}
	}

	public Connection getConnection() throws SQLException {
		try {
			//System.out.println("Path database: "+  pathDB);
			return DriverManager.getConnection(pathDB);
		} catch (SQLException e) {
			throw new SQLException("Cannot get connection to " + pathDB, e);
		}
	}
}
