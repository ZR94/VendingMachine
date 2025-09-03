package managementService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import models.Revenue;
import utils.DBManagement;

public class RevenueDAO {
	public static Revenue getMachineRevenue(Integer machine_id) {
		String query = "SELECT * FROM revenue WHERE machine_idr=?";
		Revenue result = new Revenue(0, 0);
		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			st.setInt(1, machine_id);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("machine_idr");
				Double revenue = rs.getDouble("amount");
				result.setMachineId(id);
				result.setAmount(revenue);
				;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static ArrayList<Revenue> getInstituteRevenues(int instituteId) {
		final String query = "SELECT m.machine_id, SUM(r.amount) AS total_revenue " + "FROM revenue r "
				+ "JOIN machines_info m ON r.machine_idr = m.machine_id " + "WHERE m.institute_id = ? "
				+ "GROUP BY m.machine_id";

		ArrayList<Revenue> result = new ArrayList<>();

		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			st.setInt(1, instituteId);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Integer id = rs.getInt("machine_idr");
				Double revenue = rs.getDouble("total_revenue");
				result.add(new Revenue(id, revenue));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void updateRevenue(Revenue newRevenue) {

		final String updateQuery = "UPDATE revenue SET amount = amount + ? WHERE machine_idr = ?";

		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

			updateStmt.setDouble(1, newRevenue.getAmount());
			updateStmt.setInt(2, newRevenue.getMachineId());
			int rowsUpdated = updateStmt.executeUpdate();

			if (rowsUpdated > 0) {
				System.out.println("Revenue aggiornata con successo!");
			} else {
				System.out
						.println("Nessuna riga aggiornata. Verificare se esiste la riga con machine_idr specificato.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
