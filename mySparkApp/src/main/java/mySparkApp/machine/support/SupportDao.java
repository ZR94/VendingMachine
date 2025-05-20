package mySparkApp.machine.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mySparkApp.DBConnect;
import mySparkApp.Dao;
import mySparkApp.managment.Status;

public class SupportDao {

    Dao dao = new Dao();

    public Status getMachineStatus(int idMachine) throws SQLException {
        
        String dbName = dao.retrieveDbNameByIdMachine(idMachine);
        String sql = "SELECT status FROM maintenance" + dbName;
        Status status = null;
        try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        status = Status.valueOf(rs.getString("status"));
                    }
        } catch (SQLException e) {
            System.out.println("Errore durante la query: " + e.getMessage());
            throw e;
        }
        return status;
    }

    public void updateMachineStatus(int idMachine, Status newStatus) throws SQLException {

        String dbName = dao.retrieveDbNameByIdMachine(idMachine);
        String sql = "UPDATE maintenance" + dbName + " SET status = ?";
        try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, newStatus.name());
                    int rowsUpdated = pstmt.executeUpdate();
                if(rowsUpdated > 0) {
                    System.out.println("Stato aggiornato con successo a " + newStatus);
                }
        } catch (SQLException e) {
            System.out.println("Errore durante l'aggiornamento dello stato: " + e.getMessage());
        }
    }

    public void fullPods(int idMachine) throws SQLException {
        String dbName = dao.retrieveDbNameByIdMachine(idMachine);
        String sql = "UPDATE pod" + dbName + " SET quantity = 100";
        try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento delle pods: " + e.getMessage());
        }
    }

    //Da controllare, specilamente le query
    public void emptyCash(int idMachine) throws SQLException {
        String dbName = dao.retrieveDbNameByIdMachine(idMachine);
        double credit = 0.0;
        String sql = "UPDATE cashRegister" + dbName + " SET credit = 0";
        String updateSql = "UPDATE cashRegister" + dbName + " SET credit = 0";
        try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Errore durante lo svuotamento della cassa: " + e.getMessage());
        }
    }
}
