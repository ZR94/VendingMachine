package displayService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import models.Beverage;
import utils.DB;
import java.sql.Connection;
import java.sql.SQLException;

public class DisplayDao {

	public ArrayList<Beverage> getAllDrink() {

		String query = "SELECT * FROM beverage";

		ArrayList<Beverage> result = new ArrayList<>();

		try (Connection conn = DB.getInstance().getConnection(); PreparedStatement st = conn.prepareStatement(query)) {

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("beverage_id");
				String name = rs.getString("name");
				Double price = rs.getDouble("price");
				Beverage item = new Beverage(id, name, price);
				// System.out.println(item.toString());
				result.add(item);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	// In DisplayDao.java
	public String getMachineStatus(int machineId) {
		String sql = "SELECT status FROM machine WHERE machine_id = ?";
		try (Connection conn = DB.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, machineId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("status");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // in caso di errore
	}

}
