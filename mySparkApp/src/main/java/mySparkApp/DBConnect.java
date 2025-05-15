package mySparkApp;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.UUID;

public class DBConnect {
    static private final String dbPL= "jdbc:mysql://localhost:3306";
    static private final String dbServerManagement= "jdbc:mysql://localhost:3306/dbServer";
    static private final String user = "root";
    static private final String password = "123456Zz";
    private static DBConnect instance = null;
    private static Dao dao = new Dao();

    private DBConnect() {}

    /**
     * Returns the singleton instance of the DBConnect class.
     * If the instance does not exist, it initializes a new one.
     * This method is synchronized to ensure thread safety.
     * 
     * @return the singleton instance of DBConnect
     */

    public static synchronized DBConnect getInstance() {
        if (instance == null) {
            instance = new DBConnect();
        }
        return instance;
    }

    /**
     * Gets a connection to the database for the server.
     * @return a connection to the database for the server
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbPL, user, password);
    }

    /**
     * Gets a connection to the database for the management server.
     * @return a connection to the database for the management server
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnectionServer() throws SQLException {
        return DriverManager.getConnection(dbServerManagement, user, password);
    }

    /**
     * Gets a connection to the database for the given coffee machine.
     * @param idMachine the id of the coffee machine
     * @return a connection to the database for the given coffee machine
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnectionByIdMachine(int idMachine) throws SQLException {

        String result = dao.retrieveDbNameByIdMachine(idMachine);
        String dbName = "jdbc:mysql://localhost:3306/" + result;
        
        return DriverManager.getConnection(dbName, user, password);

    }


}

/*
        public static void main(String[] args) {
        try {

            DBConnect MySQLServer = DBConnect.getInstance();
            MySQLServer.createNewCoffeeMachineDb(1);

            //Connection con1 = MySQLServer.getConnectionServer();
 

 
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
  
 */