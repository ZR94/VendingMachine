package mySparkApp;
import static spark.Spark.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.asn1.pkcs.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.*;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.eclipse.paho.client.mqttv3.*;
import mySparkApp.SSLUtils;
import mySparkApp.administration.AdministrationTopics;
import mySparkApp.administration.Institute;
import mySparkApp.administration.InstituteDao;
import mySparkApp.administration.MachineDao;
import mySparkApp.administration.Revenue;
import mySparkApp.administration.RevenueDao;
import mySparkApp.administration.Role;
import mySparkApp.administration.User;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import mySparkApp.machine.ControllerCoffeeMachine;
import mySparkApp.machine.Status;
import mySparkApp.machine.utils.MqttModClient;

public class MySparkApp {
	
	static Gson gson = new Gson();
	private static MqttModClient mqttClient;
	static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

		setup();
		setupRoutes();
        //get("/login", (request, response) -> "");
        //get("/", (request, response) -> "root");
    }
    
	public static void setup() {

		String serverUrl = "ssl://localhost:8883";
    	String home = System.getProperty("user.home");
		String caFilePath = home + "/Documents/VisualStudioCodeProject/MachineCoffeeProject/mySparkApp/TLS/ca/ca.crt";
    	String clientCrtFilePath = home + "/Documents/VisualStudioCodeProject/MachineCoffeeProject/mySparkApp/TLS/client/client.crt";
    	String clientKeyFilePath = home + "/Documents/VisualStudioCodeProject/MachineCoffeeProject/mySparkApp/TLS/client/client.key";
		
		String mqttUserName = "management"; String mqttPassword = "management";
		String clientId = "ManagementService";

		try {
			mqttClient = new MqttModClient(serverUrl, caFilePath,clientCrtFilePath, clientKeyFilePath, mqttUserName, mqttPassword, clientId);
			mqttClient.connect();

			mqttClient.subscribe(AdministrationTopics.MANAGEMENT_MACHINE_REVENUE_RECEIVE, (topic, msg) -> {
				String payloadString = new String(msg.getPayload());
				System.out.println("Ricevuto revenue da macchinetta: "+ payloadString);

				Double revenue = Double.valueOf(payloadString);

				String[] topicLevels = topic.split("/");
				String machineIdStr = topicLevels[2];
				Integer machineId = Integer.valueOf(machineIdStr);

				RevenueDao.updateRevenue(new Revenue(machineId, revenue));

			});

			mqttClient.subscribe(AdministrationTopics.MANAGEMENT_MACHINE_STATUS_RECEIVE, (topic, msg) -> {
				String payloadString = new String(msg.getPayload());
				System.out.println("Ricevuto stato da macchinetta: "+ payloadString);

				Status newStatus = Status.valueOf(payloadString.toUpperCase());

				String[] topicLevels = topic.split("/");
				String machineIdStr = topicLevels[2];
				Integer machineId = Integer.valueOf(machineIdStr);

				MachineDao.updateMachineStatus(machineId, newStatus);
			});
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static void setupRoutes() {
		path("/api/v1.0", () -> {

			// Registrazione
			post("/register", (req, res) -> {
				res.type("application/json");

				User user = gson.fromJson(req.body(), User.class);
				if (user.getUsername() == null || user.getPassword() == null || user.getRole() == null) {
					res.status(400);
					Map<String, String> error = new HashMap<>();
					error.put("error", "Missing required fields");
					return gson.toJson(error);
				}

				boolean success = Authentication.register(user.getUsername(), user.getPassword(), user.getRole());

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

				String sessionToken = Authentication.login(username, password);
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

				// Authentication Authentication = new Authentication();
				Authentication.logout(sessionToken);
				Map<String, String> message = new HashMap<>();
				message.put("message", "Logged out successfully");
				return gson.toJson(message);
			});

			// prendi i dati dell'utente loggato
			get("/user", (req, res) -> {
				res.type("application/json");

				String sessionToken = req.headers("Authorization");
				if (sessionToken == null || sessionToken.isEmpty()) {
					res.status(401);
					Map<String, String> error = new HashMap<>();
					error.put("error", "Unauthorized");
					return gson.toJson(error);
				}

				User user = Authentication.getLoggedUser(sessionToken);

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
				User user = Authentication.getLoggedUser(sessionId);
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
					return gson.toJson(InstituteDao.getInstitutes());
				});

				// Dettagli di un istituto in particolare
				get("/:id_institute", (req, res) -> {
					int instituteId = Integer.parseInt(req.params(":id_institute"));
					Institute institute = InstituteDao.getInstituteById(instituteId);
					ArrayList<Revenue> revenues = RevenueDao.getInstituteRevenues(instituteId);

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
					User user=Authentication.getLoggedUser(session);
					if (Role.ADMIN.equals(user.getRole())==false) {
						halt(403, "Forbidden");
					}

					try {
						Institute institute = gson.fromJson(req.body(), Institute.class);
						InstituteDao.addInstitute(institute.getName());
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
						User user = Authentication.getLoggedUser(session);
						if (Role.ADMIN.equals(user.getRole())==false) {
							halt(403, "Forbidden");
						}
						int instituteId = Integer.parseInt(req.params(":id_institute"));
						Boolean result = InstituteDao.deleteInstitute(instituteId);
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
						User user=Authentication.getLoggedUser(session);
						if (Role.ADMIN.equals(user.getRole())==false) {
							halt(403, "Forbidden");
						}

						int instituteId = Integer.parseInt(req.params(":id_institute"));
						ControllerCoffeeMachine machine = new ControllerCoffeeMachine(instituteId);
						MachineDao.addMachine(machine);
						res.status(201);
						return gson.toJson(machine);
					});

					// Vedere tutte le macchinette di un istituto
					get("", (req, res) -> {
						int instituteId = Integer.parseInt(req.params(":id_institute"));
						res.type("application/json");
						return gson.toJson(MachineDao.getMachinesByInstituteId(instituteId));
					});

					// vedere una macchinetta in particolare dell'istituto
					get("/:id_machine", (req, res) -> {
						int machineId = Integer.parseInt(req.params(":id_machine"));
						int instituteId = Integer.parseInt(req.params(":id_institute"));
						ControllerCoffeeMachine machine = MachineDao.getMachineById(machineId);
						if (machine != null && machine.getIdInstitute() == instituteId) {
							Map<String, Object> responseMap = new HashMap<>();
							responseMap.put("machine", machine);
							Revenue rev = RevenueDao.getMachineRevenue(machineId);
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
							ControllerCoffeeMachine machine = MachineDao.getMachineById(machineId);
							if (machine != null) {
								res.type("text/plain");
								if (machine.getStatus().equals(Status.ACTIVE) == false) {

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
							User user=Authentication.getLoggedUser(session);
							if (Role.ADMIN.equals(user.getRole())==false) {
								halt(403, "Forbidden");
							}
							int machineId = Integer.parseInt(req.params(":id_machine"));
							Boolean result = MachineDao.deleteMachine(machineId);
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
					return gson.toJson(MachineDao.getAllMachines());
				});

				// vedere i dettagli di una macchinetta in generale
				get("/:id_machine", (req, res) -> {
					int machineId = Integer.parseInt(req.params(":id_machine"));
					ControllerCoffeeMachine machine = MachineDao.getMachineById(machineId);
					if (machine != null) {
						Map<String, Object> responseMap = new HashMap<>();
						responseMap.put("machine", machine);
						Revenue rev = RevenueDao.getMachineRevenue(machineId);
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
					User user=Authentication.getLoggedUser(session);
					if (Role.ADMIN.equals(user.getRole())==false) {
						halt(403, "Forbidden");
					}
					ControllerCoffeeMachine machine = gson.fromJson(req.body(), ControllerCoffeeMachine.class);
					MachineDao.addMachine(machine);
					res.status(201);
					return gson.toJson(machine);
				});

				path("/:id_machine", () -> {

					// inviare qualcuno a risolvere lo status non attivo di una macchina
					post("", (req, res) -> {
						int machineId = Integer.parseInt(req.params(":id_machine"));
						ControllerCoffeeMachine machine = MachineDao.getMachineById(machineId);
						if (machine != null) {
							res.type("text/plain");
							if (machine.getStatus().equals(Status.ACTIVE) == false) {

								mqttClient.publish("/machine/" + machineId + "/management/statusUpdate/receive",
										new byte[0]);
								res.status(200);
								
								MachineDao.updateMachineStatus(machineId, Status.ACTIVE);

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
						User user=Authentication.getLoggedUser(session);
						if (Role.ADMIN.equals(user.getRole())==false) {
							halt(403, "Forbidden");
						}
						int machineId = Integer.parseInt(req.params(":id_machine"));
						Boolean result = MachineDao.deleteMachine(machineId);
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
