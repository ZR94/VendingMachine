package managementService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import models.MachineManagement;
import models.MachineStatus;
import models.StatusMachine;
import utils.DBManagement;
import java.sql.Connection;
import java.sql.SQLException;

public class MachineDAO {

	public static void addMachine(MachineManagement machine) {

		final String checkInstituteQuery = "SELECT 1 FROM institute WHERE institute_id = ?";
		final String getMaxIdQuery = "SELECT MAX(machine_id) FROM machines_info WHERE institute_id = ?";
		final String insertQuery = "INSERT INTO machines_info (machine_id, institute_id, status) VALUES (?, ?, ?)";
		final String insertRevenueSql = "INSERT INTO revenue (machine_idr, amount) VALUES (?, 0)";

		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement checkInstituteStmt = conn.prepareStatement(checkInstituteQuery);
				PreparedStatement getMaxIdStmt = conn.prepareStatement(getMaxIdQuery);
				PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
				PreparedStatement insertRevStmt = conn.prepareStatement(insertRevenueSql)) {

			int instituteId = machine.getInstituteId();
			checkInstituteStmt.setInt(1, instituteId);

			try (ResultSet rs = checkInstituteStmt.executeQuery()) {

				if (!rs.next()) {
					System.out.println("Institute with ID " + instituteId + " does not exist.");
					return;
				}
			}

			int newMachineId = instituteId * 100 + 1;

			getMaxIdStmt.setInt(1, instituteId);

			try (ResultSet rs = getMaxIdStmt.executeQuery()) {
				if (rs.next() && rs.getInt(1) != 0) {
					newMachineId = rs.getInt(1) + 1;
				}
			}

			insertStmt.setInt(1, newMachineId);
			insertStmt.setInt(2, instituteId);
			insertStmt.setString(3, StatusMachine.ACTIVE.toString());
			insertStmt.executeUpdate();
			
			insertRevStmt.setInt(1, newMachineId);
			insertRevStmt.executeUpdate();

			machine.setId(newMachineId);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean deleteMachine(int id) {

		final String query = "DELETE FROM machines_info WHERE machine_id = ?";

		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			st.setInt(1, id);

			int affectedRows = st.executeUpdate();

			return affectedRows > 0;

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return false;
	}

	public static MachineManagement getMachineById(int id) {

		final String query = "SELECT * FROM machines_info WHERE machine_id = ?";

		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			st.setInt(1, id);

			try (ResultSet rs = st.executeQuery()) {

				if (rs.next()) {

					return new MachineManagement(rs.getInt("machine_id"), rs.getInt("institute_id"),
							MachineStatus.valueOf(rs.getString("status").toUpperCase()));
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return null;
	}

	public static ArrayList<MachineManagement> getMachinesByInstituteId(int instituteId) {

		final String query = "SELECT * FROM machines_info WHERE institute_id = ?";

		ArrayList<MachineManagement> machines = new ArrayList<>();

		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			st.setInt(1, instituteId);

			try (ResultSet rs = st.executeQuery()) {

				while (rs.next()) {

					MachineManagement machine = new MachineManagement(rs.getInt("machine_id"),
							rs.getInt("institute_id"), MachineStatus.valueOf(rs.getString("status").toUpperCase()));
					machines.add(machine);
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return machines;
	}

	public static void updateMachineStatus(int machineId, MachineStatus status) {

		final String sql = "UPDATE machines_info SET status = ? WHERE machine_id = ?";

		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(sql)) {

			st.setString(1, status.name());
			st.setInt(2, machineId);
			st.executeUpdate();

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public static MachineStatus getStatus(int machineId) {

		final String query = "SELECT status FROM machines_info WHERE machine_id = ?";

		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			st.setInt(1, machineId);

			try (ResultSet rs = st.executeQuery()) {

				if (rs.next()) {

					String statusValue = rs.getString("status").toUpperCase();

					return MachineStatus.valueOf(statusValue);
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return null;
	}

	public static ArrayList<MachineManagement> getAllMachines() {
		final String query = "SELECT * FROM machines_info";
		ArrayList<MachineManagement> allMachines = new ArrayList<>();

		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {
			ResultSet rs = st.executeQuery();
			while (rs.next()) {

				MachineManagement machine = new MachineManagement(rs.getInt("machine_id"), rs.getInt("institute_id"),
						MachineStatus.valueOf(rs.getString("status").toUpperCase()));
				allMachines.add(machine);
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return allMachines;

	}

}
