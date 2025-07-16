package mySparkApp.machine.buttonPanel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import mySparkApp.DBConnect;
import mySparkApp.Dao;
import mySparkApp.machine.Beverage;

public class ButtonPanelDao {

    public ArrayList<Beverage> getAllBeverages(int idMachine) throws SQLException {
        
        ArrayList<Beverage> beverages = new ArrayList<Beverage>();

        String dbName = Dao.getDbNameFromIdMachine(idMachine);
        String sql = "SELECT * FROM beverage" + dbName;

        try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Beverage beverage = new Beverage();
                beverage.setIdBeverage(rs.getInt("beverage_id"));
                beverage.setName(rs.getString("name"));
                beverage.setPrice(rs.getDouble("price"));
                beverages.add(beverage);
            }
            
        } catch (Exception e) {
            System.out.println("Errore durante la query: " + e.getMessage());
        }

        return beverages;
    }

    public String getMachineStatus(int machineId) {
		String sql = "SELECT status FROM coffeeMachine WHERE idMachine = ?";
		try (Connection conn = DBConnect.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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
