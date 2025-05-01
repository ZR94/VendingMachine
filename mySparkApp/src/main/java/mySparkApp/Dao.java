package mySparkApp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Dao {
    private DBConnect dbConnect; // DBConnect instance for database connection

    public Dao() {
        // Initialize DBConnect instance
        dbConnect = DBConnect.getInstance();
    }

    public Map<Integer, Double> retrievePrices() {
        Map<Integer, Double> prices = new HashMap<>();
        try (Connection connection = dbConnect.getConnection(); Statement statement = connection.createStatement(); 
            ResultSet resultSet = statement.executeQuery("SELECT beverage_id, price FROM price_table")) {
            while (resultSet.next()) {
                int beverageId = resultSet.getInt("beverage_id");
                double price = resultSet.getDouble("price");
                prices.put(beverageId, price);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving prices from MySQL database: " + e.getMessage());
        }
        return prices;
    }

    public Map<Integer, Integer> retrieveQuantities() {
        Map<Integer, Integer> quantities = new HashMap<>();
        try (Connection connection = dbConnect.getConnection(); Statement statement = connection.createStatement(); 
            ResultSet resultSet = statement.executeQuery("SELECT beverage_id, quantity FROM quantity_table")) {
            while (resultSet.next()) {
                int beverageId = resultSet.getInt("beverage_id");
                int quantity = resultSet.getInt("quantity");
                quantities.put(beverageId, quantity);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving quantities from MySQL database: " + e.getMessage());
        }
        return quantities;
    }

    public Map<Integer, Integer> retrieveSugarQuantities() {
        Map<Integer, Integer> sugarQuantities = new HashMap<>();
        try (Connection connection = dbConnect.getConnection(); Statement statement = connection.createStatement(); 
            ResultSet resultSet = statement.executeQuery("SELECT sugar_id, quantity FROM sugar_table")) {
            while (resultSet.next()) {
                int sugarId = resultSet.getInt("sugar_id");
                int quantity = resultSet.getInt("quantity");
                sugarQuantities.put(sugarId, quantity);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving sugar quantities from MySQL database: " + e.getMessage());
        }
        return sugarQuantities;
    }

    public Map<Integer, Integer> retrievePaperGlassQuantities() {
        Map<Integer, Integer> paperGlassQuantities = new HashMap<>();
        try (Connection connection = dbConnect.getConnection(); 
            Statement statement = connection.createStatement(); 
            ResultSet resultSet = statement.executeQuery("SELECT paper_glass_id, quantity FROM paper_glass_table")) {
            while (resultSet.next()) {
                int paperGlassId = resultSet.getInt("paper_glass_id");
                int quantity = resultSet.getInt("quantity");
                paperGlassQuantities.put(paperGlassId, quantity);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving paper glass quantities from MySQL database: " + e.getMessage());
        }
        return paperGlassQuantities;
    }

    public boolean isBeverageAvailable(int beverageId) {
        try (Connection connection = dbConnect.getConnection(); Statement statement = connection.createStatement(); 
            ResultSet resultSet = statement.executeQuery("SELECT quantity FROM quantity_table WHERE beverage_id = " + beverageId)) {
            if (resultSet.next()) {
                int quantity = resultSet.getInt("quantity");
                return quantity > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking beverage availability: " + e.getMessage());
        }
        return false;
    }

    public boolean isSugarQuantityAvailable(int sugarQuantity) {
        try (Connection connection = dbConnect.getConnection(); 
            Statement statement = connection.createStatement(); 
            ResultSet resultSet = statement.executeQuery("SELECT quantity FROM sugar_table WHERE sugar_id = 1")) {
            if (resultSet.next()) {
                int quantity = resultSet.getInt("quantity");
                return quantity >= sugarQuantity;
            }
        } catch (SQLException e) {
            System.out.println("Error checking sugar quantity availability: " + e.getMessage());
        }
        return false;
    }

    public boolean isPaperGlassQuantityAvailable(int paperGlassQuantity) {
        try (Connection connection = dbConnect.getConnection(); 
            Statement statement = connection.createStatement(); 
            ResultSet resultSet = statement.executeQuery("SELECT quantity FROM paper_glass_table WHERE paper_glass_id = 1")) {
            if (resultSet.next()) {
                int quantity = resultSet.getInt("quantity");
                return quantity >= paperGlassQuantity;
            }
        } catch (SQLException e) {
            System.out.println("Error checking paper glass quantity availability: " + e.getMessage());
        }
        return false;
    }
}
