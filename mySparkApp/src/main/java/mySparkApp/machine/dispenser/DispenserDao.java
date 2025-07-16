package mySparkApp.machine.dispenser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mySparkApp.DBConnect;
import mySparkApp.Dao;

public class DispenserDao {

    // Da controllare, string sql cosi non funziona
    public boolean checkPods(int idMachine, int idBeverage) throws SQLException{

        String dbName = Dao.getDbNameFromIdMachine(idMachine);

        final String sql = "SELECT bp.pod_id, bp.pod_required, p.current_qty " + "FROM beverage_pod bp "
				+ "JOIN pod p ON bp.pod_id = p.pod_id " + "WHERE bp.beverage_id = ?";
        
        try {
            Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idBeverage);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int podId = rs.getInt("bp.pod_id");
                int podRequired = rs.getInt("bp.pod_required");
                int currentQty = rs.getInt("p.current_qty");
                if (currentQty < podRequired) {
                    return false;
                }
            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }

        return true;
    }

    public double getBeveragePrice(int idMachine, int idBeverage) throws SQLException {
        String dbName = Dao.getDbNameFromIdMachine(idMachine);
	    final String sql = "SELECT price" + dbName + " FROM beverage WHERE beverage_id = ?";
	    double price = 0.0;

	    try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, idBeverage);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            price = rs.getDouble("price");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return price;
	}

    public void deductPodsForBeverage(int idMachine, int idBeverage) throws SQLException {

        String dbName = Dao.getDbNameFromIdMachine(idMachine);

		// Decremento la quantità di cialde dal DB
		final String selectSql = "SELECT bp.idPod, bp.podRequired " + dbName + "FROM beveragePod bp "
				+ "WHERE bp.idBeverage = ?";

		final String updateSql = "UPDATE pod SET currentQuantity = currentQuantity - ? WHERE idPod = ?";

		try (Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement stSelect = conn.prepareStatement(selectSql);
				PreparedStatement stUpdate = conn.prepareStatement(updateSql)) {

			stSelect.setInt(1, idBeverage);
			ResultSet rs = stSelect.executeQuery();
			while (rs.next()) {
				int podId = rs.getInt("idPod");
				int required = rs.getInt("podRequired");
				// Eseguo l'UPDATE per ogni cialda necessaria
				stUpdate.setInt(1, required);
				stUpdate.setInt(2, podId);
				stUpdate.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isAnyPodLow() {
	    String sql = "SELECT COUNT(*) as cnt FROM pod WHERE currentQuantity <= 5";
	    try (Connection conn = DBConnect.getInstance().getConnection();
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
