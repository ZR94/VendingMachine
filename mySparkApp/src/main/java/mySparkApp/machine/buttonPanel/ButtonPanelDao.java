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
    
    Dao dao = new Dao();

    public ArrayList<Beverage> getAllBeverages(int idMachine) throws SQLException {
        
        ArrayList<Beverage> beverages = new ArrayList<Beverage>();

        String dbName = dao.retrieveDbNameByIdMachine(idMachine);
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
}
