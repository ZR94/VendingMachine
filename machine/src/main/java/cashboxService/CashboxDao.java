package cashboxService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import models.Machine;
import models.StatusMachine;
import utils.DB;

public class CashboxDao {

	/**
	 * Aggiunge (somma) l'importo indicato al credito corrente (current_credit)
	 * della macchina con l'ID specificato.
	 *
	 * @param machineId ID della macchina a cui aggiornare il credito
	 * @param amount    Importo da aggiungere al credito corrente
	 */
	public void updateCredit(int machineId, double amount) {
		String sql = "UPDATE machine " + "SET current_credit = current_credit + ? " + "WHERE machine_id = ?";

		try (Connection conn = DB.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setDouble(1, amount);
			ps.setInt(2, machineId);

			int rowsUpdated = ps.executeUpdate();
			if (rowsUpdated == 0) {
				System.out.println("Nessuna macchina trovata con machine_id = " + machineId);
			} else {
				System.out.println("Credito aggiornato con successo per la macchina " + machineId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public double getCredit(int machineId) {
		final String sql = "SELECT current_credit FROM machine WHERE machine_id = ?";
		double credit = 0.0;

		try (Connection conn = DB.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(sql)) {

			st.setInt(1, machineId);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				credit = rs.getDouble("current_credit");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return credit;
	}
	
	public void resetCredit(int machineId) {
		String sql = "UPDATE machine " + "SET current_credit = 0 " + "WHERE machine_id = ?";

		try (Connection conn = DB.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, machineId);

			int rowsUpdated = ps.executeUpdate();
			if (rowsUpdated == 0) {
				System.out.println("Nessuna macchina trovata con machine_id = " + machineId);
			} else {
				System.out.println("Credito corrente resettato con successo per la macchinea " + machineId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean payBeveragePrice(int machineId, double price) {
	    String selectSql = "SELECT current_credit, total_cash FROM machine WHERE machine_id = ?";
	    String updateSql = "UPDATE machine SET current_credit = current_credit - ?, total_cash = total_cash + ? WHERE machine_id = ?";

	    try (Connection conn = DB.getInstance().getConnection();
	         PreparedStatement stSel = conn.prepareStatement(selectSql);
	         PreparedStatement stUpd = conn.prepareStatement(updateSql)) {

	        stSel.setInt(1, machineId);
	        ResultSet rs = stSel.executeQuery();
	        if (rs.next()) {
	            double currCredit = rs.getDouble("current_credit");
	            if (currCredit < price) {
	                // credito insufficiente
	                return false;
	            }
	            // se sufficiente, procedo all'update
	            stUpd.setDouble(1, price);
	            stUpd.setDouble(2, price);
	            stUpd.setInt(3, machineId);
	            stUpd.executeUpdate();
	            return true;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    // Se qualcosa va storto
	    return false;
	}
	
	public Machine getMachineData(int machineId) {
		final String sql = "SELECT * FROM machine WHERE machine_id = ?";
		Machine result = new Machine();

		try (Connection conn = DB.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(sql)) {

			st.setInt(1, machineId);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				result.setMachineId(machineId); 
				result.setTotalCash(rs.getDouble("total_cash"));
				result.setMaxCash(rs.getDouble("max_cash")); ;
				result.setCurrentCredit(rs.getDouble("current_credit"));
				result.setStatus(StatusMachine.fromString(rs.getString("status")));
				//result.setStatus(StatusMachine.valueOf(rs.getString("status").toUpperCase()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
