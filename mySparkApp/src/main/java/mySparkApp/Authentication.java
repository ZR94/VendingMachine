package mySparkApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

import mySparkApp.administration.Role;
import mySparkApp.administration.User;



public class Authentication {

    	private static Map<String, User> sessions = new HashMap<>();

	public static boolean register(String username, String password, Role role) {
		final String checkUserQuery = "SELECT COUNT(*) FROM users WHERE username=?";
		final String insertUserQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

		try (Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery);
				PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery)) {

			checkStmt.setString(1, username);
			try (ResultSet rs = checkStmt.executeQuery()) {
				if (rs.next() && rs.getInt(1) > 0) {
					return false;
				}
			}

			String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

			insertStmt.setString(1, username);
			insertStmt.setString(2, hashedPassword);
			insertStmt.setString(3, role.name());

			int rowsAffected = insertStmt.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// login
	public static String login(String username, String password) {
		final String query = "SELECT * FROM users WHERE username=?";
		try (Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			st.setString(1, username);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				String dbPassword = rs.getString("password");
				if (BCrypt.checkpw(password, dbPassword)) {
					String session = generateSessionToken(username);
					sessions.put(session,
							new User(rs.getInt("user_Id"), username, password, Role.valueOf(rs.getString("role"))));
					return session;
				} else {
					return null;
				}

			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return null;
	}

	// logout
	public static void logout(String session) {
		sessions.remove(session);
	}

	// GETTER
	public static User getLoggedUser(String session) {
		if (sessions.containsKey(session)) {
			return sessions.get(session);
		} else {
			return null;
		}

	}

	private static String generateSessionToken(String username) {
		String newSession=username+sessions.size();
		while(sessions.containsKey(newSession)) {
			newSession=newSession+sessions.size();
		}
		return newSession;
	}
    
}
