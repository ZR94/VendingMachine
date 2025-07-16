package mySparkApp;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.UUID;
import java.util.ResourceBundle.Control;

import mySparkApp.machine.ControllerCoffeeMachine;
import mySparkApp.machine.Status;

public class Dao {
    
    /**
     * Creates a new coffee machine database and a new user with privileges
     * on that database.
     * 
     * @param institutionId the id of the institution that owns the machine
     * @return the id of the newly created coffee machine
     * @throws SQLException if a database access error occurs
     */
    public static int createCoffeeMachineDb(int institutionId) throws SQLException {
        String uuid = UUID.randomUUID().toString();
        int status = 1;
        String dbName = "coffeeMachineDb_" + uuid.replace("-", "_");
        String dbUser = "user_" + uuid.substring(0, 8);
        String dbPassword = generateSecurePassword();
        int result = -1;

        try (Connection conn = DBConnect.getInstance().getConnection();
                Statement stmt = conn.createStatement()) {

            // Crea il database
            stmt.executeUpdate("CREATE DATABASE " + dbName);

            // Crea utente e permessi
            stmt.executeUpdate("CREATE USER '" + dbUser + "'@'%' IDENTIFIED BY '" + dbPassword + "'");
            stmt.executeUpdate("GRANT ALL PRIVILEGES ON " + dbName + ".* TO '" + dbUser + "'@'%'");
            
            // Crea tabelle nel nuovo database
            createNewDbSchema(dbName);

            conn.close();
            
            Connection connServer = DBConnect.getInstance().getConnectionServer();
            // Registra nel management database
            
            String insertSql = "INSERT INTO coffeeMachine (idMachineInstitute, status, dbName, dbUser, dbPassword) " +
                                "VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = connServer.prepareStatement(insertSql)) {
                pstmt.setInt(1, institutionId);
                pstmt.setInt(2, status);
                pstmt.setString(3, dbName);
                pstmt.setString(4, dbUser);
                pstmt.setString(5, dbPassword);
                pstmt.executeUpdate();
                connServer.close();

                // 3. Recupera l'ID generato
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    result = generatedKeys.getInt(1); // Restituisce l'idMachine
                }
                
            } catch (SQLException e) {
                System.out.println("Errore: " + e.getMessage());
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Errore: " + e.getMessage());
            throw e;
        }
        return result;
    }

    /**
     * Cancella il database corrispondente all'idMachine specificato.
     * @param idMachine l'id della macchina da cancellare
     * @throws SQLException se si verifica un errore durante l'esecuzione della query
     */
    public static void deleteCoffeeMachineDb(int idMachine) throws SQLException {
        // Recupera il nome del database da cancellare
        String dbName = getDbNameFromIdMachine(idMachine);
    
        if (dbName != null) {
            // Crea una connessione al database
            Connection conn = DBConnect.getInstance().getConnection();
    
            // Crea un statement per eseguire la query
            Statement stmt = conn.createStatement();
    
            // Query per cancellare il database
            String query = "DROP DATABASE " + dbName;
    
            // Esegue la query
            stmt.executeUpdate(query);
    
            // Chiude la connessione
            conn.close();
    
            // Crea una connessione al database di gestione
            Connection connServer = DBConnect.getInstance().getConnectionServer();
    
            // Crea un statement per eseguire la query
            Statement stmtServer = connServer.createStatement();
    
            // Query per cancellare la riga corrispondente nella tabella coffeeMachine
            String queryDelete = "DELETE FROM coffeeMachine WHERE idMachine = " + idMachine;
    
            // Esegue la query
            stmtServer.executeUpdate(queryDelete);
    
            // Chiude la connessione
            connServer.close();
        } else {
            System.out.println("Database non trovato");
        }
    }

    /**
     * Retrieves the name of the database associated with the given idMachine.
     * @param idMachine the id of the coffee machine
     * @return the name of the database associated with the given idMachine, or null if not found
     * @throws SQLException if a database access error occurs
     */
    public static String getDbNameFromIdMachine(int idMachine) throws SQLException {
        // Crea una connessione al database di gestione
        Connection connServer = DBConnect.getInstance().getConnectionServer();
    
        // Crea un statement per eseguire la query
        Statement stmt = connServer.createStatement();
    
        // Query per recuperare il nome del database
        String query = "SELECT dbName FROM coffeeMachine WHERE idMachine = " + idMachine;
    
        // Esegue la query
        ResultSet rs = stmt.executeQuery(query);
    
        // Recupera il nome del database
        String dbName = null;
        if (rs.next()) {
            dbName = rs.getString("dbName");
        }
    
        // Chiude la connessione
        connServer.close();
    
        return dbName;
    }

    /**
     * Crea le tabelle per un nuovo database.
     * @param dbName Il nome del database
     * @throws SQLException Se si verifica un errore durante la creazione delle tabelle
     */
    private static void createNewDbSchema(String dbName) throws SQLException {
        // Query per creare il database se non esiste
        String createDbSql = "CREATE DATABASE IF NOT EXISTS " + dbName;
        
        // Query per selezionare il database
        String useDbSql = "USE " + dbName;
        
        // Query per creare le tabelle
        String[] createTables = {
            "CREATE TABLE IF NOT EXISTS pod (" +
                "idPod INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "currentQuantity INT NOT NULL, " +
                "maxQuantity INT NOT NULL" +  // Rimosso virgola finale superflua
            ") ENGINE=InnoDB",
        
            "CREATE TABLE IF NOT EXISTS beverage (" +
                "idBeverage INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL UNIQUE, " +
                "price DECIMAL(5,2) NOT NULL CHECK (price >= 0)" +  // Rimosso virgola finale
            ") ENGINE=InnoDB",
        
            "CREATE TABLE IF NOT EXISTS beveragePod (" +
                "idBeverage INT, " +
                "idPod INT, " +
                "podRequired INT NOT NULL CHECK (podRequired > 0), " +  // Corretto "quantity" -> "podRequired"
                "PRIMARY KEY (idBeverage, idPod), " +
                "FOREIGN KEY (idBeverage) REFERENCES beverage(idBeverage) ON DELETE CASCADE, " +
                "FOREIGN KEY (idPod) REFERENCES pod(idPod) ON DELETE CASCADE" +
            ") ENGINE=InnoDB",
        
            "CREATE TABLE IF NOT EXISTS cashRegister (" +
                "idCashRegister INT AUTO_INCREMENT PRIMARY KEY, " +
                "credit DECIMAL(10,2) NOT NULL DEFAULT 0.00, " +
                "last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB",
        
            "CREATE TABLE IF NOT EXISTS maintenance (" +
                "idMaintenance INT AUTO_INCREMENT PRIMARY KEY, " +
                "status ENUM('REPORTED', 'IN_PROGRESS', 'RESOLVED') NOT NULL DEFAULT 'REPORTED', " +
                "description TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "resolved_at DATETIME" +  // Rimosso campo "resolved" ridondante con status
            ") ENGINE=InnoDB"
        };
    
        try (Connection conn = DBConnect.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Crea il database se non esiste
            stmt.executeUpdate(createDbSql);
            
            // 2. Seleziona il database
            stmt.executeUpdate(useDbSql);
            
            // 3. Crea tutte le tabelle
            for (String sql : createTables) {
                stmt.executeUpdate(sql);
            }
            
            System.out.println("Schema del database '" + dbName + "' creato con successo");
            
        } catch (SQLException e) {
            System.err.println("Errore nella creazione dello schema per il database '" + dbName + "': " + e.getMessage());
            throw e; // Rilancia l'eccezione per gestione esterna
        }
    }

    /**
     * Generates a secure password for the coffee machine database.
     * 
     * @return a secure password
     */
    private static String generateSecurePassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[12];
        random.nextBytes(bytes);
        return new String(Base64.getEncoder().encode(bytes));
    }



}
