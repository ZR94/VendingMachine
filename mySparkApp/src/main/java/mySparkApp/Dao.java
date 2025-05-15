package mySparkApp;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.UUID;

public class Dao {
    
    public String retrieveDbNameByIdMachine(int idMachine) throws SQLException {
        // Esegue una query per recuperare il dbName dal database
        final String query = "SELECT dbName FROM coffeeMachine WHERE idMachine = ?";

        try {
            Connection conn = DBConnect.getInstance().getConnectionServer();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idMachine);
            ResultSet rs = pstmt.executeQuery();
            conn.close();

            if (rs.next()) {
                return rs.getString("dbName");
            } else {
                throw new SQLException("Machine non trovata");
            }

        } catch (SQLException e) {
            System.out.println("Errore durante la query: " + e.getMessage());
            throw e;
        }
    }

    public void createNewCoffeeMachineDb(int institutionId) throws SQLException {
        String uuid = UUID.randomUUID().toString();
        String dbName = "coffeeMachineDb_" + uuid.replace("-", "_");
        String dbUser = "user_" + uuid.substring(0, 8);
        String dbPassword = generateSecurePassword();

        try (Connection conn = DBConnect.getInstance().getConnection();
                Statement stmt = conn.createStatement()) {

            // Crea il database
            stmt.executeUpdate("CREATE DATABASE " + dbName);

            // Crea utente e permessi
            stmt.executeUpdate("CREATE USER '" + dbUser + "'@'%' IDENTIFIED BY '" + dbPassword + "'");
            stmt.executeUpdate("GRANT ALL PRIVILEGES ON " + dbName + ".* TO '" + dbUser + "'@'%'");
            
            // Crea tabelle nel nuovo database
            this.createNewDbSchema(dbName);

            conn.close();
            
            Connection connServer = DBConnect.getInstance().getConnectionServer();
            // Registra nel management database
            
            String insertSql = "INSERT INTO coffeeMachine (idMachineInstitute, dbName, dbUser, dbPassword) " +
                                "VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = connServer.prepareStatement(insertSql)) {
                pstmt.setInt(1, institutionId);
                pstmt.setString(2, dbName);
                pstmt.setString(3, dbUser);
                pstmt.setString(4, dbPassword);
                pstmt.executeUpdate();

                connServer.close();
            } catch (SQLException e) {
                System.out.println("Errore: " + e.getMessage());
            }
            

        } catch (SQLException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    public void createNewDbSchema(String dbName) throws SQLException {
        String[] createTables = {
            "CREATE TABLE " + dbName + ".pods ("
                + "idProduct INTEGER PRIMARY KEY, AUTO_INCREMENT, "
                + "quantity INT NOT NULL CHECK (quantity >= 0), "
                + "last_restock DATETIME)",
                
            "CREATE TABLE " + dbName + ".cashRegister ("
                + "idCashRegister INTEGER PRIMARY KEY AUTO_INCREMENT, "
                + "idCashRegisterProduct INT NOT NULL, "
                + "quantity INT NOT NULL, "
                + "price DECIMAL(5,2) NOT NULL, "
                + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)",

            "CREATE TABLE " + dbName + ".price ("
                + "idPrice INTEGER PRIMARY KEY AUTO_INCREMENT, "
                + "idPriceProduct INT NOT NULL, "
                + "prezzo DECIMAL(5,2) NOT NULL, "
                + "data_inizio DATE NOT NULL, "
                + "data_fine DATE, "
                + "FOREIGN KEY (idPriceProduct) REFERENCES " + dbName + ".pods(idProduct))" ,   
                
            "CREATE TABLE " + dbName + ".maintenance ("
                + "idMaintenance INTEGER PRIMARY KEY AUTO_INCREMENT, "
                + "description TEXT, "
                + "resolved BOOLEAN DEFAULT false, "
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)"
        };

        try (Connection conn = DBConnect.getInstance().getConnection();
                Statement stmt = conn.createStatement()) {
            for (String sql : createTables) {
                stmt.executeUpdate(sql);
            }
            conn.close(); 
        } catch (SQLException e) {
            System.out.println("Errore: " + e.getMessage());
            throw e;
        }
    }

        private String generateSecurePassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[12];
        random.nextBytes(bytes);
        return new String(Base64.getEncoder().encode(bytes));
    }

}
