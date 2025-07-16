package mySparkApp.machine.cashRegister;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mySparkApp.DBConnect;
import mySparkApp.Dao;
import mySparkApp.machine.ControllerCoffeeMachine;
import mySparkApp.machine.Status;

public class CashRegisterDao {
 
    public void updateCredit(int idMachine, double amount) throws SQLException{

        String dbName = null;
        try {
            dbName = Dao.getDbNameFromIdMachine(idMachine);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    
        if (dbName == null) {
            System.out.println("Database non trovato per idMachine: " + idMachine);
            return;
        }

        String sql = "UPDATE cashRegister" + dbName + " SET credit = credit + ?";
        
        try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setDouble(1, amount);
                    
                    int rowsUpdated = pstmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        System.out.println("Credit aggiornato con successo.");
                    } else {
                        System.out.println("Nessuna riga aggiornata.");
                    }
        } catch (SQLException e) {
            System.out.println("Errore durante l'aggiornamento del credit: " + e.getMessage());
            throw e;
        }
    }

    public double getCredit(int idMachine) throws SQLException{
        String dbName = null;
        try {
            dbName = Dao.getDbNameFromIdMachine(idMachine);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        String sql = "SELECT credit FROM cashRegister" + dbName;
        double credit = 0.0;
        try (Connection conn = DBConnect.getInstance().getConnectionByIdMachine(idMachine);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        credit = rs.getDouble("credit");
                    }
        } catch (SQLException e) {
            System.out.println("Errore durante la query: " + e.getMessage());
            throw e;
        }
        return credit;
    }

    public void resetCredit(int machineId) {
        String dbName = null;
        try {
            dbName = Dao.getDbNameFromIdMachine(machineId);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    
        if (dbName == null) {
            System.out.println("Database non trovato per machineId: " + machineId);
            return;
        }
    
        // Query per resettare il credito nel database specifico della macchina
        String sql = "UPDATE " + dbName + ".cashRegister SET credit = 0";
    
        try (Connection conn = DBConnect.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
    
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("Nessuna riga trovata in cash_register per il database " + dbName);
            } else {
                System.out.println("Credito corrente resettato con successo per la macchina " + machineId + " (DB: " + dbName + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public boolean payBeveragePrice(int machineId, double price) {

        String dbName = null;
        try {
            dbName = Dao.getDbNameFromIdMachine(machineId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    
        if (dbName == null) {
            System.out.println("Database non trovato per machineId: " + machineId);
            return false;
        }

	    String selectSql = "SELECT current_credit, total_cash FROM"  + dbName + " .cashRegister";
	    String updateSql = "UPDATE " + dbName + ".cashRegister SET credit = current_credit - ?, total_cash = total_cash + ? ";

	    try (Connection conn = DBConnect.getInstance().getConnection();
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
	            stUpd.executeUpdate();
	            return true;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    // Se qualcosa va storto
	    return false;
	}

    //Controllare
    public ControllerCoffeeMachine getMachineData(int machineId) {
		final String sql = "SELECT * FROM machine WHERE machine_id = ?";
		ControllerCoffeeMachine result = new ControllerCoffeeMachine();

		try (Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(sql)) {

			st.setInt(1, machineId);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				result.setIdMachine(machineId);
				result.setTotalCash(rs.getDouble("total_cash"));
				result.setMaxCash(rs.getDouble("max_cash")); ;
				result.setCurrentCredit(rs.getDouble("current_credit"));
				result.setStatus(Status.fromString(rs.getString("status")));
				//result.setStatus(StatusMachine.valueOf(rs.getString("status").toUpperCase()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
