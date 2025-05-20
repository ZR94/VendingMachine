package mySparkApp.machine.cashRegister;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mySparkApp.DBConnect;
import mySparkApp.Dao;

public class CashRegisterDao {

    Dao dao = new Dao();
    
    public void updateCredit(int idMachine, double amount) throws SQLException{

        String dbName = dao.retrieveDbNameByIdMachine(idMachine);

        String sql = "UPDATE cashRegister" + dbName + " SET credit = credit + ?";
        
        try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setDouble(1, amount);
                    
                    int rowsUpdated = pstmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        System.out.println("Credit aggiornato con successo.");
                    } else {
                        System.out.println("Nessuna riga aggiornata.");
                    }
        } catch (SQLException e) {
            System.out.println("Errore durante l'aggiornamento del credit: " + e.getMessage());
            throw e;
        }
    }

    public double getCredit(int idMachine) throws SQLException{
        String dbName = dao.retrieveDbNameByIdMachine(idMachine);
        String sql = "SELECT credit FROM cashRegister" + dbName;
        double credit = 0.0;
        try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        credit = rs.getDouble("credit");
                    }
        } catch (SQLException e) {
            System.out.println("Errore durante la query: " + e.getMessage());
            throw e;
        }
        return credit;
    }
}
