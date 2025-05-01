package mySparkApp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ButtonPanel {
    private Map<Integer, Double> prices; // Map to store prices of beverages
    private Map<Integer, Integer> quantities; // Map to store quantities of beverages
    private Map<Integer, Integer> sugarQuantities; // Map to store quantities of sugar
    private Map<Integer, Integer> paperGlassQuantities; // Map to store quantities of paper glasses
    private double insertedMoney; // Variable to store the money inserted by the user
    private DBConnect dbConnect; // DBConnect instance for database connection
    private int selectedBeverageId; // Variable to store the selected beverage ID
    private int selectedSugarQuantity; // Variable to store the selected sugar quantity
    private int selectedPaperGlassQuantity; // Variable to store the selected paper glass quantity

    public ButtonPanel() {
        prices = new HashMap<>();
        quantities = new HashMap<>();
        sugarQuantities = new HashMap<>();
        paperGlassQuantities = new HashMap<>();
        insertedMoney = 0.0;

        // Initialize DBConnect instance
        dbConnect = DBConnect.getInstance();

        // Retrieve the prices, quantities, sugar quantities, and paper glass quantities from the MySQL database
        retrievePricesAndQuantitiesFromDatabase();
    }

    private void retrievePricesAndQuantitiesFromDatabase() {
        try (Connection connection = dbConnect.getConnection(); 
            Statement statement = connection.createStatement()) {
                
            ResultSet priceResultSet = statement.executeQuery("SELECT beverage_id, price FROM price_table");
            while (priceResultSet.next()) {
                int beverageId = priceResultSet.getInt("beverage_id");
                double price = priceResultSet.getDouble("price");
                prices.put(beverageId, price);
            }

            ResultSet quantityResultSet = statement.executeQuery("SELECT beverage_id, quantity FROM quantity_table");
            while (quantityResultSet.next()) {
                int beverageId = quantityResultSet.getInt("beverage_id");
                int quantity = quantityResultSet.getInt("quantity");
                quantities.put(beverageId, quantity);
            }

            ResultSet sugarQuantityResultSet = statement.executeQuery("SELECT sugar_id, quantity FROM sugar_table");
            while (sugarQuantityResultSet.next()) {
                int sugarId = sugarQuantityResultSet.getInt("sugar_id");
                int quantity = sugarQuantityResultSet.getInt("quantity");
                sugarQuantities.put(sugarId, quantity);
            }

            ResultSet paperGlassQuantityResultSet = statement.executeQuery("SELECT paper_glass_id, quantity FROM paper_glass_table");
            while (paperGlassQuantityResultSet.next()) {
                int paperGlassId = paperGlassQuantityResultSet.getInt("paper_glass_id");
                int quantity = paperGlassQuantityResultSet.getInt("quantity");
                paperGlassQuantities.put(paperGlassId, quantity);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving prices and quantities from MySQL database: " + e.getMessage());
        }
    }

    public void selectBeverage(int beverageId) {
        selectedBeverageId = beverageId;
    }

    public void selectSugarQuantity(int sugarQuantity) {
        selectedSugarQuantity = sugarQuantity;
    }

    public void selectPaperGlassQuantity(int paperGlassQuantity) {
        selectedPaperGlassQuantity = paperGlassQuantity;
    }

    public void dispenseBeverage() {
        if (insertedMoney >= prices.get(selectedBeverageId)) {
            // Check if the beverage is available in the database
            if (isBeverageAvailable(selectedBeverageId)) {
                // Check if the selected sugar quantity is available in the database
                if (isSugarQuantityAvailable(selectedSugarQuantity)) {
                    // Check if the selected paper glass quantity is available in the database
                    if (isPaperGlassQuantityAvailable(selectedPaperGlassQuantity)) {
                        // Dispense the beverage
                        System.out.println("Dispensing beverage " + selectedBeverageId);
                        insertedMoney -= prices.get(selectedBeverageId);
                        System.out.println("Remaining balance: " + insertedMoney);
                    } else {
                        System.out.println("Sorry, the selected paper glass quantity is not available.");
                    }
                } else {
                    System.out.println("Sorry, the selected sugar quantity is not available.");
                }
            } else {
                System.out.println("Sorry, the beverage is not available.");
            }
        } else {
            System.out.println("Insufficient balance. Please insert more money.");
        }
    }

    private boolean isBeverageAvailable(int beverageId) {
        return quantities.get(beverageId) > 0;
    }

    private boolean isSugarQuantityAvailable(int sugarQuantity) {
        return sugarQuantities.get(1) >= sugarQuantity; // Assuming sugar ID is
    }

    private boolean isPaperGlassQuantityAvailable(int paperGlassQuantity) {
        return paperGlassQuantities.get(1) >= paperGlassQuantity; // Assuming paper glass ID is 1
    }
}