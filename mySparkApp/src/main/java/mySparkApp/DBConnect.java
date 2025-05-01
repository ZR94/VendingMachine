package mySparkApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {
    static private final String dbLoc = "jdbc:mysql://localhost:3306/dbTest";
    static private final String user = "root";
    static private final String password = "123456Zz";
    private static DBConnect instance = null;

    private DBConnect() {}

    public static synchronized DBConnect getInstance() {
        if (instance == null) {
            instance = new DBConnect();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbLoc, user, password);
    }

    public static void main(String[] args) {
        try {
            DBConnect dbConnect = DBConnect.getInstance();
            Connection con = dbConnect.getConnection();
            Statement st = con.createStatement();
            st.execute("SELECT 1");
            con.close();
            System.out.println("Connessione riuscita!");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}