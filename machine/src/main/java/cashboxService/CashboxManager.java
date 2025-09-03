package cashboxService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import com.google.gson.Gson;

import models.Machine;
import utils.MQTTClient;

public class CashboxManager {

	static Gson gson = new Gson();
	private static int machine_id = 100;
	private static MQTTClient mqttClient;
	private static CashboxDao dao;
	private static Machine objMachine;

	public static void main(String[] args) {
		System.out.println("***Cashbox Manager Console ID: " + machine_id + " ***");
		dao = new CashboxDao();
		setupMQTT();
	}

	public static <mqttClient> void setupMQTT() {
		// MQTT Client Configuration
		// da impostare poi con ssl al posto di tcp quando si implementa connessione TLS
		String serverUrl = "ssl://localhost:8883";
		
		String caFilePath = Paths.get("").toAbsolutePath()
                .resolve("certs")
                .resolve("ca")
                .resolve("ca.crt")
                .toString();

        String clientCrtFilePath = Paths.get("").toAbsolutePath()
                .resolve("certs")
                .resolve("client")
                .resolve("client.crt")
                .toString();

        String clientKeyFilePath = Paths.get("").toAbsolutePath()
                .resolve("certs")
                .resolve("client")
                .resolve("client.key")
                .toString();
        
        String mqttUserName = "cash"; String mqttPassword = "cash";
		 
		String clientId = "Cashbox Service :" + machine_id;

		mqttClient = new MQTTClient(serverUrl, caFilePath,clientCrtFilePath, clientKeyFilePath, mqttUserName, mqttPassword, clientId);
		mqttClient.connect();

		mqttClient.subscribe(String.format(TopicsCashbox.CASHBOX_INSERT_COIN_REQ, machine_id), (topic, msg) -> {
		    // Estrazione dell'ID della macchina dal topic
		    String[] topicLevels = topic.split("/");
		    String machineIdStr = topicLevels[2];
		    int machineId = Integer.parseInt(machineIdStr);

		    // Converte il payload a stringa
		    String payloadString = new String(msg.getPayload(), StandardCharsets.UTF_8);
		    System.out.println("Coin received: " + payloadString);

		    // Converte la stringa in double
		    double coinValue = Double.parseDouble(payloadString);

		    // Passa il valore convertito al metodo Dao
		    dao.updateCredit(machineId, coinValue);
		});
		
		mqttClient.subscribe(String.format(TopicsCashbox.CASHBOX_CURR_CREDIT_REQ, machine_id), (topic, msg) -> {
		    // Estrazione dell'ID della macchina dal topic
		    String[] topicLevels = topic.split("/");
		    String machineIdStr = topicLevels[2];
		    int machineId = Integer.parseInt(machineIdStr);

		    // Converte il payload a stringa
		    String payloadStringRec = new String(msg.getPayload(), StandardCharsets.UTF_8);
		    System.out.println(payloadStringRec);

		    // Converte la stringa in double
		    double credit = dao.getCredit(machineId);

		    String payloadString = String.valueOf(credit);
            byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);

            mqttClient.publish(String.format(TopicsCashbox.CASHBOX_CURR_CREDIT_RES, machine_id), payloadBytes);
		});
		
		mqttClient.subscribe(String.format(TopicsCashbox.CASHBOX_RETURN_CREDIT_REQ, machine_id), (topic, msg) -> {
		    // Estrazione dell'ID della macchina dal topic
		    String[] topicLevels = topic.split("/");
		    String machineIdStr = topicLevels[2];
		    int machineId = Integer.parseInt(machineIdStr);

		    // Converte il payload a stringa
		    String payloadStringRec = new String(msg.getPayload(), StandardCharsets.UTF_8);
		    System.out.println(payloadStringRec);

		    // Converte la stringa in double
		    double credit = dao.getCredit(machineId);
		    dao.resetCredit(machineId);

		    String payloadString = String.valueOf(credit);
            byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);

            mqttClient.publish(String.format(TopicsCashbox.CASHBOX_RETURN_CREDIT_RES, machine_id), payloadBytes);
		});
		
		mqttClient.subscribe(String.format(TopicsCashbox.CASHBOX_VERIFY_CREDIT_REQ, machine_id), (topic, msg) -> {
		    // Ricavo l'ID macchina dal topic
		    String[] topicLevels = topic.split("/");
		    String machineIdStr = topicLevels[2];
		    int machineId = Integer.parseInt(machineIdStr);

		    // Recupero dal payload il prezzo richiesto per la bevanda
		    String payloadStringRec = new String(msg.getPayload(), StandardCharsets.UTF_8);
		    double price = Double.parseDouble(payloadStringRec);
		    System.out.println("[CashboxManager] Verifica credito per price=" + price);

		    // Verifico e, se sufficiente, scalo il credito e aggiorno la cassa
		    boolean result = dao.payBeveragePrice(machineId, price);
		 
		    if (result) {
		        // Pagamento OK, verifichiamo la cassa
		        objMachine = dao.getMachineData(machineId);

		        if (objMachine.getTotalCash() >= (objMachine.getMaxCash() - 5)) {
		            System.out.println("[CashboxManager] Raggiunta soglia, segnalo full_cash all'Assistance.");
		            byte[] payload = "full_cash".getBytes(StandardCharsets.UTF_8);
		            mqttClient.publish(
		                String.format(TopicsCashbox.CASHBOX_STATUS_NOTIFY, machineId),
		                payload
		            );
		        }
		    }


		    // Preparo il payload di risposta
		    String response = result ? "OK" : "INSUFFICIENT_CREDIT";
		    byte[] payloadResp = response.getBytes(StandardCharsets.UTF_8);

		    // Pubblico la risposta al topic bevande
		    mqttClient.publish(String.format(TopicsCashbox.CASHBOX_VERIFY_CREDIT_RES, machineId), payloadResp);
		});


		/* Altro modo per fare la stessa roba cosa senza lambda function
		 * 
		 * mqttClient.subscribe("/cashboxService/" + machine_id + "/insert/request", new
		 * IMqttMessageListener() {
		 * 
		 * @Override public void messageArrived(String topic, MqttMessage msg) throws
		 * Exception { String[] topicLevels = topic.split("/"); String machineIdStr =
		 * topicLevels[2]; int machineId = Integer.parseInt(machineIdStr);
		 * 
		 * String payloadString = new String(msg.getPayload(), StandardCharsets.UTF_8);
		 * System.out.println(payloadString); // ... } });
		 */
	}

}
