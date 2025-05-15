package mySparkApp;

public class User {
    
    private int userId;
    private String username;
    private String password;
    private Role role;
    
    
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }


    public User(int userId, String username, String password, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }


    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public Role getRole() {
        return role;
    }


    @Override
    public String toString() {
        return "User [userId=" + userId + ", username=" + username + ", password=" + password + ", role=" + role + "]";
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (userId != other.userId)
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (role != other.role)
            return false;
        return true;
    }


}
