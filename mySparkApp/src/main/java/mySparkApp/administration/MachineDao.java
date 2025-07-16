package mySparkApp.administration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import mySparkApp.DBConnect;
import mySparkApp.machine.ControllerCoffeeMachine;
import mySparkApp.machine.Status;

public class MachineDao {

    public void addMachine(ControllerCoffeeMachine machine) {

        
        
        
    }

    /**
     * Deletes a coffee machine from the database.
     * @param idMachine the ID of the machine to be deleted
     * @return true if the machine was successfully deleted, false otherwise
	 * questa cosa la fa gia deleteCoffeeMachineDb in Dao, da rivedere i due metodi
     */
    public static boolean deleteMachine(int idMachine) {

		final String query = "DELETE FROM coffeeMachine WHERE idMachine = ?";

		try (Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			st.setInt(1, idMachine);

			int affectedRows = st.executeUpdate();

			return affectedRows > 0;

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Updates the status of a coffee machine in the database.
	 * @param machineId the ID of the machine to be updated
	 * @param status the new status of the machine
	 */
    public static void updateMachineStatus(int machineId, Status status) {

		final String sql = "UPDATE coffeeMachine SET statusMachine = ? WHERE idMachine = ?";

		try (Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(sql)) {

			st.setString(1, status.name());
			st.setInt(2, machineId);
			st.executeUpdate();

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Gets the status of a coffee machine from the database.
	 * @param machineId the ID of the machine to retrieve the status of
	 * @return the status of the machine or null if no machine with the given ID exists
	 */
    public static Status getStatus(int machineId) {

		final String query = "SELECT statusMachine FROM coffeeMachine WHERE idMachine = ?";

		try (Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			st.setInt(1, machineId);

			try (ResultSet rs = st.executeQuery()) {

				if (rs.next()) {

					String statusValue = rs.getString("status").toUpperCase();

					return Status.valueOf(statusValue);
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Gets all coffee machines from the database.
	 * @return an ArrayList containing all the coffee machines, each represented as a ControllerCoffeeMachine object
	 */
    public static ArrayList<ControllerCoffeeMachine> getAllMachines() {
		final String query = "SELECT * FROM coffeeMachine";
		ArrayList<ControllerCoffeeMachine> allMachines = new ArrayList<>();

		try (Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {
			ResultSet rs = st.executeQuery();
			while (rs.next()) {

				ControllerCoffeeMachine machine = new ControllerCoffeeMachine(rs.getInt("idMachine"), rs.getInt("idInstitute"),
						Status.valueOf(rs.getString("status").toUpperCase()));
				allMachines.add(machine);
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return allMachines;

	}

	/**
	 * Gets all coffee machines belonging to the given institution from the database.
	 * @param instituteId the id of the institution
	 * @return an ArrayList containing all the coffee machines belonging to the given institution, each represented as a ControllerCoffeeMachine object
	 */
    public static ArrayList<ControllerCoffeeMachine> getMachinesByInstituteId(int instituteId) {

		final String query = "SELECT * FROM coffeeMachine WHERE idInstitute = ?";

		ArrayList<ControllerCoffeeMachine> machines = new ArrayList<>();

		try (Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			st.setInt(1, instituteId);

			try (ResultSet rs = st.executeQuery()) {

				while (rs.next()) {

					ControllerCoffeeMachine machine = new ControllerCoffeeMachine(rs.getInt("idMachine"),
							rs.getInt("idInstitute"), Status.valueOf(rs.getString("status").toUpperCase()));
					machines.add(machine);
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return machines;
	}

	/**
	 * Gets a coffee machine by its id from the database.
	 * @param id the id of the coffee machine
	 * @return the coffee machine, represented as a ControllerCoffeeMachine object, or null if not found
	 */
    public static ControllerCoffeeMachine getMachineById(int id) {

		final String query = "SELECT * FROM coffeeMachine WHERE idMachine = ?";

		try (Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			st.setInt(1, id);

			try (ResultSet rs = st.executeQuery()) {

				if (rs.next()) {

					return new ControllerCoffeeMachine(rs.getInt("idMachine"), rs.getInt("idInstitute"),
							Status.valueOf(rs.getString("status").toUpperCase()));
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return null;
	}


}
