package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManagement {
//	private static String absolutePathDB = Paths.get("").toAbsolutePath()
//		    .resolve("resources")
//		    .resolve("management.db")
//		    .toString();

//	static private final String pathDB = "jdbc:sqlite:" + absolutePathDB;
	
	static private final String pathDB = "jdbc:sqlite:src/main/resources/management.db";
	static private DBManagement database = null;

	private DBManagement() {
		database = this;
	}

	public static DBManagement getInstance() {
		if (database == null)
			return new DBManagement();
		else {
			return database;
		}
	}

	public Connection getConnection() throws SQLException {
		try {
			return DriverManager.getConnection(pathDB);
		} catch (SQLException e) {
			throw new SQLException("Cannot get connection to " + pathDB, e);
		}
	}
}
