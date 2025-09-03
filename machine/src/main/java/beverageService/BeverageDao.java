package beverageService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import models.Beverage;
import utils.DB;

public class BeverageDao {

	public boolean checkPodsForBeverage(int beverageId) {
		// Query per sapere di quali cialde (pod) e di quante unità ha bisogno la
		// bevanda
		final String sql = "SELECT bp.pod_id, bp.pod_required, p.current_qty " + "FROM beverage_pod bp "
				+ "JOIN pod p ON bp.pod_id = p.pod_id " + "WHERE bp.beverage_id = ?";

		try (Connection conn = DB.getInstance().getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {

			st.setInt(1, beverageId);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				int required = rs.getInt("pod_required");
				int current = rs.getInt("current_qty");
				if (current < required) {
					// Se una sola delle cialde necessarie non è sufficiente, ritorno false
					return false;
				}
			}
			// Se arrivo qui, significa che tutte le cialde necessarie sono sufficienti
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void deductPodsForBeverage(int beverageId) {
		// Decremento la quantità di cialde dal DB
		final String selectSql = "SELECT bp.pod_id, bp.pod_required " + "FROM beverage_pod bp "
				+ "WHERE bp.beverage_id = ?";

		final String updateSql = "UPDATE pod SET current_qty = current_qty - ? WHERE pod_id = ?";

		try (Connection conn = DB.getInstance().getConnection();
				PreparedStatement stSelect = conn.prepareStatement(selectSql);
				PreparedStatement stUpdate = conn.prepareStatement(updateSql)) {

			stSelect.setInt(1, beverageId);
			ResultSet rs = stSelect.executeQuery();
			while (rs.next()) {
				int podId = rs.getInt("pod_id");
				int required = rs.getInt("pod_required");
				// Eseguo l'UPDATE per ogni cialda necessaria
				stUpdate.setInt(1, required);
				stUpdate.setInt(2, podId);
				stUpdate.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public double getBeveragePrice(int beverageId) {
	    final String sql = "SELECT price FROM beverage WHERE beverage_id = ?";
	    double price = 0.0;

	    try (Connection conn = DB.getInstance().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, beverageId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            price = rs.getDouble("price");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return price;
	}
	
	// Dentro BeverageDao (o PodDao se preferisci separare):
	public boolean isAnyPodLow() {
	    String sql = "SELECT COUNT(*) as cnt FROM pod WHERE current_qty <= 5";
	    try (Connection conn = DB.getInstance().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            int count = rs.getInt("cnt");
	            return (count > 0);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

}
