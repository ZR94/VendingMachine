package assistanceService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import com.google.gson.Gson;

import beverageService.TopicsBeverage;
import models.StatusMachine;
import utils.MQTTClient;

public class AssistanceManager {

	static Gson gson = new Gson();
	private static int machine_id = 100;
	private static MQTTClient mqttClient;
	private static AssistanceDao dao;

	public static void main(String[] args) {
		System.out.println("***Assistance Manager Console ID: " + machine_id + " ***");
		dao = new AssistanceDao();
		setupMQTT();
	}

	public static <mqttClient> void setupMQTT() {
		// MQTT Client Configuration
		// da impostare poi con ssl al posto di tcp quando si implementa connessione TLS
		String serverUrl = "ssl://localhost:8883";

		String caFilePath = Paths.get("").toAbsolutePath().resolve("certs").resolve("ca").resolve("ca.crt").toString();

		String clientCrtFilePath = Paths.get("").toAbsolutePath().resolve("certs").resolve("client")
				.resolve("client.crt").toString();

		String clientKeyFilePath = Paths.get("").toAbsolutePath().resolve("certs").resolve("client")
				.resolve("client.key").toString();

		String mqttUserName = "assistance";
		String mqttPassword = "assistance";

		String clientId = "Assistance Service :" + machine_id;

		mqttClient = new MQTTClient(serverUrl, caFilePath, clientCrtFilePath, clientKeyFilePath, mqttUserName,
				mqttPassword, clientId);
		mqttClient.connect();

		// 1) Sottoscrizione al topic di update/notify
		mqttClient.subscribe(String.format(TopicsAssistance.ASSISTANCE_STATUS_NOTIFY, machine_id), (topic, msg) -> {
			String payloadString = new String(msg.getPayload());
			System.out.println("[AssistanceManager] Ricevuto stato di errore: " + payloadString);

			String[] topicLevels = topic.split("/");
			String machineIdStr = topicLevels[2];
			int machineId = Integer.parseInt(machineIdStr);

			// 3) Aggiorniamo lo stato nel DB (es. 'faulty','low_pods','full_cash', 'active'
			dao.updateMachineStatus(machineId, payloadString);

			// aggiorno il management
			byte[] payloadResp = payloadString.getBytes(StandardCharsets.UTF_8);
			mqttClient.publish(String.format(TopicsAssistance.ASSISTANCE_SEND_EXT, machine_id), payloadResp);
		});

		// 1) Sottoscrizione al topic di receive dal management
		mqttClient.subscribe(String.format(TopicsAssistance.ASSISTANCE_RECEIVE_EXT, machine_id), (topic, msg) -> {
			String payloadString = new String(msg.getPayload());
			System.out.println("[AssistanceManager] Ricevuto tecnico dal management: " + payloadString);

			String[] topicLevels = topic.split("/");
			String machineIdStr = topicLevels[2];
			int machineId = Integer.parseInt(machineIdStr);

			// dao.refillPods();
			// dao.updateMachineStatus(machineId, StatusMachine.ACTIVE.getValue());
			// Ora provo a implementare (Mirko)
			if (machineId == machine_id) {
				switch (dao.getMachineStatus(machineId)) {
				case FAULTY: {
					dao.updateMachineStatus(machineId, StatusMachine.ACTIVE.getValue());
					byte[] payloadResp = StatusMachine.ACTIVE.getValue().getBytes(StandardCharsets.UTF_8);
					mqttClient.publish(String.format(TopicsAssistance.ASSISTANCE_SEND_EXT, machine_id), payloadResp);
				}
				case LOW_PODS: {
					dao.refillPods();
					dao.updateMachineStatus(machineId, StatusMachine.ACTIVE.getValue());
					byte[] payloadResp = StatusMachine.ACTIVE.getValue().getBytes(StandardCharsets.UTF_8);
					mqttClient.publish(String.format(TopicsAssistance.ASSISTANCE_SEND_EXT, machine_id), payloadResp);
				}
				case FULL_CASH: {
					Double cashRemoved = dao.emptyCash(machineId);
					byte[] cashSent = cashRemoved.toString().getBytes(StandardCharsets.UTF_8);
					mqttClient.publish(String.format(TopicsAssistance.ASSISTANCE_WITHDRAW_RES_EXT, machine_id),
							cashSent);
					dao.updateMachineStatus(machineId, StatusMachine.ACTIVE.getValue());
					byte[] payloadResp = StatusMachine.ACTIVE.getValue().getBytes(StandardCharsets.UTF_8);
					mqttClient.publish(String.format(TopicsAssistance.ASSISTANCE_SEND_EXT, machine_id), payloadResp);
				}
				default:
					;
				}
			}

		});

	}

}
