package managementService;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import authenticationService.AuthService;
import models.User;
import models.UserRole;
import models.Institute;
import models.MachineManagement;
import models.MachineStatus;
import models.Revenue;
import utils.MQTTClient;

public class ManagementService {

	static Gson gson = new Gson();
	private static MQTTClient mqttClient;

	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		;
		// fare l'autenticazione
		port(3000);
		setupMQTT();
		setupRoutes();

	}

	public static <mqttClient> void setupMQTT() {
		// Configurazione del client MQTT
		String serverUrl = "ssl://localhost:8883";
		
		  String caFilePath = "src/main/resources/certs/ca/ca.crt"; 
		  String clientCrtFilePath = "src/main/resources/certs/client/client.crt"; 
		  String clientKeyFilePath = "src/main/resources/certs/client/client.key"; 
		  
		  String mqttUserName = "management"; String mqttPassword = "management";
		 
		String clientId = "ManagementService";

		mqttClient = new MQTTClient(serverUrl, caFilePath,clientCrtFilePath, clientKeyFilePath, mqttUserName, mqttPassword, clientId);
		mqttClient.connect();

		mqttClient.subscribe(TopicsManagement.MANAGEMENT_MACHINE_REVENUE_RECEIVE, (topic, msg) -> {
			String payloadString = new String(msg.getPayload());
			System.out.println("Ricevuto revenue da macchinetta: "+ payloadString);

			Double revenue = Double.valueOf(payloadString);

			String[] topicLevels = topic.split("/");
			String machineIdStr = topicLevels[2];
			Integer machineId = Integer.valueOf(machineIdStr);

			RevenueDAO.updateRevenue(new Revenue(machineId, revenue));

		});

		mqttClient.subscribe(TopicsManagement.MANAGEMENT_MACHINE_STATUS_RECEIVE, (topic, msg) -> {
			String payloadString = new String(msg.getPayload());
			System.out.println("Ricevuto stato da macchinetta: "+ payloadString);

			MachineStatus newStatus = MachineStatus.valueOf(payloadString.toUpperCase());

			String[] topicLevels = topic.split("/");
			String machineIdStr = topicLevels[2];
			Integer machineId = Integer.valueOf(machineIdStr);

			MachineDAO.updateMachineStatus(machineId, newStatus);
		});
	}

	private static void setupRoutes() {
		path("/api/v1.0", () -> {

			// Registrazione
			post("/register", (req, res) -> {
				res.type("application/json");

				User user = gson.fromJson(req.body(), User.class);
				if (user.getUsername() == null || user.getPasswordHash() == null || user.getRole() == null) {
					res.status(400);
					Map<String, String> error = new HashMap<>();
					error.put("error", "Missing required fields");
					return gson.toJson(error);
				}

				boolean success = AuthService.register(user.getUsername(), user.getPasswordHash(), user.getRole());

				if (success) {
					res.status(201);
					Map<String, String> message = new HashMap<>();
					message.put("message", "User registered successfully");
					return gson.toJson(message);
				} else {
					res.status(409);
					Map<String, String> error = new HashMap<>();
					error.put("error", "User already exists");
					return gson.toJson(error);
				}
			});

			// Login
			post("/login", (req, res) -> {
				res.type("application/json");

				Map<String, String> credentials = gson.fromJson(req.body(), Map.class);
				String username = credentials.get("username");
				String password = credentials.get("password");

				if (username == null || password == null) {
					res.status(400);
					Map<String, String> error = new HashMap<>();
					error.put("error", "Username and password are required");
					return gson.toJson(error);
				}

				String sessionToken = AuthService.login(username, password);
				if (sessionToken != null) {
					Map<String, String> token = new HashMap<>();
					token.put("token", sessionToken);
					return gson.toJson(token);
				} else {
					res.status(401);
					Map<String, String> error = new HashMap<>();
					error.put("error", "Invalid credentials");
					return gson.toJson(error);
				}
			});

			// Logout
			post("/logout", (req, res) -> {
				res.type("application/json");

				String sessionToken = req.headers("Authorization");
				if (sessionToken == null || sessionToken.isEmpty()) {
					res.status(400);
					Map<String, String> error = new HashMap<>();
					error.put("error", "Missing session token");
					return gson.toJson(error);
				}

				// AuthService authService = new AuthService();
				AuthService.logout(sessionToken);
				Map<String, String> message = new HashMap<>();
				message.put("message", "Logged out successfully");
				return gson.toJson(message);
			});

			// prendi i dati dell'utente loggat0
			get("/user", (req, res) -> {
				res.type("application/json");

				String sessionToken = req.headers("Authorization");
				if (sessionToken == null || sessionToken.isEmpty()) {
					res.status(401);
					Map<String, String> error = new HashMap<>();
					error.put("error", "Unauthorized");
					return gson.toJson(error);
				}

				User user = AuthService.getLoggedUser(sessionToken);

				if (user != null) {
					return gson.toJson(user);
				} else {
					res.status(401);
					Map<String, String> error = new HashMap<>();
					error.put("error", "Invalid session");
					return gson.toJson(error);
				}
			});

			// Controllo autenticazione pre altro
			before("/*", (req, res) -> {
				String path = req.pathInfo();

				if (path.equals("/api/v1.0/login") || path.equals("/api/v1.0/register")
						|| path.equals("/api/v1.0/user")) {
					return;
				}
				String sessionId = req.headers("Authorization");
				User user = AuthService.getLoggedUser(sessionId);
				if (user == null) {
					halt(401, "Accesso negato per favore esegui il login");
				} else {
					req.attribute("user", user);
				}
			});

			path("/institutes", () -> {

				// Lista degli istituti
				get("", (req, res) -> {
					res.type("application/json");
					return gson.toJson(InstituteDAO.getInstitutes());
				});

				// Dettagli di un istituto in particolare
				get("/:id_institute", (req, res) -> {
					int instituteId = Integer.parseInt(req.params(":id_institute"));
					Institute institute = InstituteDAO.getInstituteById(instituteId);
					ArrayList<Revenue> revenues = RevenueDAO.getInstituteRevenues(instituteId);

					if (institute != null) {
						Map<String, Object> responseMap = new HashMap<>();
						responseMap.put("institute", institute);
						responseMap.put("revenue", revenues);

						res.type("application/json");
						return gson.toJson(responseMap);
					} else {
						res.status(404);
						return "Institute not found";
					}
				});

				// Aggiunta di un istituto
				post("", (req, res) -> {

					String session = req.headers("Authorization");
					User user=AuthService.getLoggedUser(session);
					if (UserRole.ADMIN.equals(user.getRole())==false) {
						halt(403, "Forbidden");
					}

					try {
						Institute institute = gson.fromJson(req.body(), Institute.class);
						InstituteDAO.addInstitute(institute.getInstituteName());
						res.status(201);
						return gson.toJson(institute);
					} catch (JsonSyntaxException e) {
						res.status(400);
						return "Invalid JSON format";
					}
				});

				path("/:id_institute", () -> {
					// Rimozione di un istituto
					delete("", (req, res) -> {
						String session = req.headers("Authorization");
						User user=AuthService.getLoggedUser(session);
						if (UserRole.ADMIN.equals(user.getRole())==false) {
							halt(403, "Forbidden");
						}
						int instituteId = Integer.parseInt(req.params(":id_institute"));
						Boolean result = InstituteDAO.deleteInstitute(instituteId);
						res.type("text/plain");
						if (result) {
							res.status(200);
							return "Institute deleted successfully";

						} else {
							res.status(500);
							return "Something went Wrong";
						}
					});

					path("/machines", () -> {
						// Aggiungere una macchinetta a un istituto
						post("", (req, res) -> {
							String session = req.headers("Authorization");
							User user=AuthService.getLoggedUser(session);
							if (UserRole.ADMIN.equals(user.getRole())==false) {
								halt(403, "Forbidden");
							}

							int instituteId = Integer.parseInt(req.params(":id_institute"));
							MachineManagement machine = new MachineManagement(instituteId);
							MachineDAO.addMachine(machine);
							res.status(201);
							return gson.toJson(machine);
						});

						// Vedere tutte le macchinette di un istituto
						get("", (req, res) -> {
							int instituteId = Integer.parseInt(req.params(":id_institute"));
							res.type("application/json");
							return gson.toJson(MachineDAO.getMachinesByInstituteId(instituteId));
						});

						// vedere una macchinetta in particolare dell'istituto
						get("/:id_machine", (req, res) -> {
							int machineId = Integer.parseInt(req.params(":id_machine"));
							int instituteId = Integer.parseInt(req.params(":id_institute"));
							MachineManagement machine = MachineDAO.getMachineById(machineId);
							if (machine != null && machine.getInstituteId() == instituteId) {
								Map<String, Object> responseMap = new HashMap<>();
								responseMap.put("machine", machine);
								Revenue rev = RevenueDAO.getMachineRevenue(machineId);
								System.out.println(rev.getAmount());
								responseMap.put("revenue", rev);
								res.type("application/json");
								return gson.toJson(responseMap);
							} else {
								res.status(404);
								return "machine not found";
							}
						});

						path("/:id_machines", () -> {

							// inviare qualcuno a risolvere lo status non attivo di una macchina di un
							// istituto
							post("", (req, res) -> {
								int machineId = Integer.parseInt(req.params(":id_machine"));
								MachineManagement machine = MachineDAO.getMachineById(machineId);
								if (machine != null) {
									res.type("text/plain");
									if (machine.getStatus().equals(MachineStatus.ACTIVE) == false) {

										mqttClient.publish("/machine/" + machineId + "/management/statusUpdate/receive",
												new byte[0]);
										res.status(200);

										return "Assistance has been sent to the machine successfully";
									} else {
										res.status(500);
										return "Machine is already active";
									}
								} else {
									res.type("application/json");
									res.status(404);
									return "machine not found";
								}

							});

							// eliminare una macchinetta
							delete("", (req, res) -> {
								
								String session = req.headers("Authorization");
								User user=AuthService.getLoggedUser(session);
								if (UserRole.ADMIN.equals(user.getRole())==false) {
									halt(403, "Forbidden");
								}
								int machineId = Integer.parseInt(req.params(":id_machine"));
								Boolean result = MachineDAO.deleteMachine(machineId);
								res.type("text/plain");
								if (result) {
									res.status(200);
									return "Institute deleted successfully";

								} else {
									res.status(500);
									return "Something went Wrong";
								}
							});
						});

					});
				});

			});

			path("/machines", () -> {

				// vedere tutte le macchinette
				get("", (req, res) -> {

					res.type("application/json");
					return gson.toJson(MachineDAO.getAllMachines());
				});

				// vedere i dettagli di una macchinetta in generale
				get("/:id_machine", (req, res) -> {
					int machineId = Integer.parseInt(req.params(":id_machine"));
					MachineManagement machine = MachineDAO.getMachineById(machineId);
					if (machine != null) {
						Map<String, Object> responseMap = new HashMap<>();
						responseMap.put("machine", machine);
						Revenue rev = RevenueDAO.getMachineRevenue(machineId);
						responseMap.put("revenue", rev);
						res.type("application/json");
						return gson.toJson(responseMap);
					} else {
						res.status(404);
						return "Machine not found";
					}
				});

				// aggiungere una macchinetta a qualsiasi istituto
				post("", (req, res) -> {
					String session = req.headers("Authorization");
					User user=AuthService.getLoggedUser(session);
					if (UserRole.ADMIN.equals(user.getRole())==false) {
						halt(403, "Forbidden");
					}
					MachineManagement machine = gson.fromJson(req.body(), MachineManagement.class);
					MachineDAO.addMachine(machine);
					res.status(201);
					return gson.toJson(machine);
				});

				path("/:id_machine", () -> {

					// inviare qualcuno a risolvere lo status non attivo di una macchina
					post("", (req, res) -> {
						int machineId = Integer.parseInt(req.params(":id_machine"));
						MachineManagement machine = MachineDAO.getMachineById(machineId);
						if (machine != null) {
							res.type("text/plain");
							if (machine.getStatus().equals(MachineStatus.ACTIVE) == false) {

								mqttClient.publish("/machine/" + machineId + "/management/statusUpdate/receive",
										new byte[0]);
								res.status(200);
								
								MachineDAO.updateMachineStatus(machineId, MachineStatus.ACTIVE);

								return "Assistance has been sent to the machine successfully";
							} else {
								res.status(500);
								return "Machine is already active";
							}
						} else {
							res.type("application/json");
							res.status(404);
							return "machine not found";
						}
					});

					// eliminare una macchinetta
					delete("", (req, res) -> {
						String session = req.headers("Authorization");
						User user=AuthService.getLoggedUser(session);
						if (UserRole.ADMIN.equals(user.getRole())==false) {
							halt(403, "Forbidden");
						}
						int machineId = Integer.parseInt(req.params(":id_machine"));
						Boolean result = MachineDAO.deleteMachine(machineId);
						res.type("text/plain");
						if (result) {
							res.status(200);
							return "Machine deleted successfully";

						} else {
							res.status(500);
							return "Something went Wrong";
						}
					});
				});

			});
		});

	}

}
