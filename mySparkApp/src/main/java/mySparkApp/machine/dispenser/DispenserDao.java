package mySparkApp.machine.dispenser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mySparkApp.DBConnect;
import mySparkApp.Dao;

public class DispenserDao {

    Dao dao = new Dao();

    // Da controllare, string sql cosi non funziona
    public boolean checkPods(int idMachine, int idBeverage) throws SQLException{

        String dbName = dao.retrieveDbNameByIdMachine(idMachine);

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

    public double getBeveragePrice(int idMachine, int beverageId) throws SQLException {
        String dbName = dao.retrieveDbNameByIdMachine(idMachine);
	    final String sql = "SELECT price" + dbName + " FROM beverage WHERE beverage_id = ?";
	    double price = 0.0;

	    try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
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



}
