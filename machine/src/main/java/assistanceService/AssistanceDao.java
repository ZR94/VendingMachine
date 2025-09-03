package assistanceService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import models.StatusMachine;
import utils.DB;

public class AssistanceDao {

	public StatusMachine getMachineStatus(int machineId) {
		String sql = "SELECT status FROM machine WHERE machine_id = ?";

		try (Connection conn = DB.getInstance().getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {

			st.setInt(1, machineId);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				String statusString = rs.getString("status");

				StatusMachine status = StatusMachine.valueOf(statusString.toUpperCase());

				return status;
			} 

		} catch (SQLException e) {
			
			System.err.println("SQL Error: " + e.getMessage());
			e.printStackTrace();
		}

		 return null;// Restituisce lo stato della macchina
	}
	
	public void updateMachineStatus(int machineId, String newStatus) {
		// newStatus sarà "faulty", "low_pods", "full_cash" o "active"
		// Controlli di validità (opzionali) per sicurezza
		if (!isValidStatus(newStatus)) {
			System.out.println("[AssistanceDao] Status non valido: " + newStatus);
			return;
		}

		String sql = "UPDATE machine SET status = ? WHERE machine_id = ?";
		try (Connection conn = DB.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, newStatus);
			ps.setInt(2, machineId);
			int rowsUpdated = ps.executeUpdate();

			if (rowsUpdated > 0) {
				System.out.println("[AssistanceDao] Stato macchina aggiornato a: " + newStatus);
			} else {
				System.out.println("[AssistanceDao] Nessuna macchina trovata con ID: " + machineId);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean isValidStatus(String status) {
		// Piccolo check su i 4 possibili status
		// oppure potresti usare il tuo enum StatusMachine.fromString(status)
		return ("faulty".equals(status) || "low_pods".equals(status) || "full_cash".equals(status)
				|| "active".equals(status));
	}

	/**
	 * 1) Refill Pods – Porta tutte le cialde (pod) al valore max_qty
	 */
	public void refillPods() {
		String sql = "UPDATE pod SET current_qty = max_qty";

		try (Connection conn = DB.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			int rowsUpdated = ps.executeUpdate();
			System.out.println("[AssistanceDao] Refill pods eseguito. Cialde riportate al max_qty per " + rowsUpdated
					+ " record.");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 2) Svuota la macchina (parametrizzata sull'ID macchina) – Azzera il valore di
	 * total_cash per la macchina indicata
	 */
	public double emptyCash(int machineId) {
		double oldValue = 0.0;

		// 1) Leggiamo il total_cash corrente
		String selectSql = "SELECT total_cash FROM machine WHERE machine_id = ?";

		// 2) Aggiorniamo la colonna total_cash a 0
		String updateSql = "UPDATE machine SET total_cash = 0 WHERE machine_id = ?";

		try (Connection conn = DB.getInstance().getConnection();
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
