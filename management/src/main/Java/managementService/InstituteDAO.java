package managementService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import models.Institute;
import utils.DBManagement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InstituteDAO {

	public static ArrayList<Institute> getInstitutes() {

		final String query = "SELECT * FROM institute";

		ArrayList<Institute> institutes = new ArrayList<>();

		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Institute institute = new Institute(rs.getInt("institute_id"), rs.getString("name"));
				institutes.add(institute);
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return institutes;
	}

	public static void addInstitute(String name) {

		final String query = "INSERT INTO institute (name) VALUES (?)";

		// GENERIAMO LE CHIAVI AUTOMATICAMENTE PER L'ID DEGLI ISTITUTI
		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			// stiamo impostando il primo parametro della query
			st.setString(1, name);
			st.executeUpdate();

			try (ResultSet generatedKeys = st.getGeneratedKeys()) {

				
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public static Institute getInstituteById(int id) {

		final String query = "SELECT * FROM institute WHERE institute_id = ?";

		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			st.setInt(1, id);

			try (ResultSet rs = st.executeQuery()) {

				if (rs.next()) {

					return new Institute(rs.getInt("institute_id"), rs.getString("name"));
				}
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return null;
	}

	// QUANDO CANCELLI UN ISTITUTO SI CANCELLANO ANCHE TUTTE LE MACCHINETTE
	// ASSOCIATE A QUELL'ISTITUTO
	// PERCHE ABBIAMO MESSO ON DELLETE CASCADE NELLA TABELLA "machines_info"
	public static boolean deleteInstitute(int id) {

		final String query = "DELETE FROM institute WHERE institute_id = ?";

		try (Connection conn = DBManagement.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(query)) {

			st.setInt(1, id);
			int affectedRows = st.executeUpdate();

			return affectedRows > 0; // return true se tutto � andato a buon fine, false altrimenti

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return false;
	}
}
