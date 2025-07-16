package mySparkApp.machine.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mySparkApp.DBConnect;
import mySparkApp.Dao;
import mySparkApp.machine.Status;

public class SupportDao {

    Dao dao = new Dao();

    public Status getMachineStatus(int idMachine) throws SQLException {
        
        String dbName = Dao.getDbNameFromIdMachine(idMachine);
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

    public void updateMachineStatus(int idMachine, String newStatus) throws SQLException {

        String dbName = Dao.getDbNameFromIdMachine(idMachine);
        String sql = "UPDATE maintenance" + dbName + " SET status = ?";
        try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, newStatus);
                    int rowsUpdated = pstmt.executeUpdate();
                if(rowsUpdated > 0) {
                    System.out.println("Stato aggiornato con successo a " + newStatus);
                }
        } catch (SQLException e) {
            System.out.println("Errore durante l'aggiornamento dello stato: " + e.getMessage());
        }
    }

    public void fullPods(int idMachine) throws SQLException {
        String dbName = Dao.getDbNameFromIdMachine(idMachine);
        String sql = "UPDATE pod" + dbName + " SET quantity = 100";
        try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento delle pods: " + e.getMessage());
        }
    }

    public void refillPods() {
		String sql = "UPDATE pod SET current_qty = max_qty";

		try (Connection conn = DBConnect.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			int rowsUpdated = ps.executeUpdate();
			System.out.println("[AssistanceDao] Refill pods eseguito. Cialde riportate al max_qty per " + rowsUpdated
					+ " record.");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    // da controllare
    public double emptyCash(int machineId) {
		double oldValue = 0.0;

		// 1) Leggiamo il total_cash corrente
		String selectSql = "SELECT total_cash FROM machine WHERE machine_id = ?";

		// 2) Aggiorniamo la colonna total_cash a 0
		String updateSql = "UPDATE machine SET total_cash = 0 WHERE machine_id = ?";

		try (Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement psSel = conn.prepareStatement(selectSql);
				PreparedStatement psUpd = conn.prepareStatement(updateSql)) {

			// Leggiamo il valore corrente
			psSel.setInt(1, machineId);
			ResultSet rs = psSel.executeQuery();
			if (rs.next()) {
				oldValue = rs.getDouble("total_cash");
			} else {
				System.out.println("[AssistanceDao] Nessuna macchina trovata con ID: " + machineId);
				return 0.0; // o -1.0 come valore di errore
			}

			// Aggiorniamo a zero
			psUpd.setInt(1, machineId);
			int rowsUpdated = psUpd.executeUpdate();

			if (rowsUpdated > 0) {
				System.out.println("[AssistanceDao] total_cash azzerato per la macchina " + machineId);
			} else {
				System.out.println("[AssistanceDao] Nessuna macchina trovata con ID: " + machineId);
				return 0.0; // o -1.0 come valore di errore
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return 0.0; // o -1.0 se preferisci segnalare errori
		}

		// Ritorniamo il vecchio valore (quello letto prima di azzerare)
		return oldValue;
	}

}
