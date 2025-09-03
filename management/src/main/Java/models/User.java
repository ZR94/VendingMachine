package models;

public class User {

	private int userId;
	private String username;
	private String password;
	private UserRole role;

	public User(String username, String password, UserRole role) {

		this.username = username;
		this.password = password;
		this.role = role;
	}

	public User(int userId, String username, String password, UserRole role) {

		this.userId = userId;
		this.username = username;
		this.password = password;
		this.role = role;
	}

	// GETTERS
	public int getId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return password;
	}

	public UserRole getRole() {
		return role;
	}

	@Override
	public String toString() {
		return "User{" + "UserId=" + userId + ", Username='" + username + ", Role=" + role + '}';
	}
}
