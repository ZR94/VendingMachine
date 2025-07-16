package mySparkApp.machine.cashRegister;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.Gson;

import mySparkApp.machine.ControllerCoffeeMachine;
import mySparkApp.machine.utils.MqttModClient;

public class CashRegister {

    static Gson gson = new Gson();
    private static int idMachine = 100;
    private static MqttModClient mqttClient;
    private static CashRegisterDao cashRegisterDao;
    private static ControllerCoffeeMachine coffeeMachine;

    public static void main(String[] args) {
		System.out.println("***Cashbox Manager Console ID: " + idMachine + " ***");
		cashRegisterDao = new CashRegisterDao();
		setupMQTT();
	}

    public static void setupMQTT() {
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
		 
		String clientId = "Cashbox Service :" + idMachine;


        try {
            mqttClient = new MqttModClient(serverUrl, clientId, mqttUserName, mqttPassword, caFilePath, clientCrtFilePath, clientKeyFilePath);
            mqttClient.connect();
            setupSubscriptions();
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void setupSubscriptions() throws MqttException {

        mqttClient.subscribe(String.format(CashRegisterTopics.CASHREGISTER_PAYMENT, idMachine), (topic, msg) -> {
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
		    cashRegisterDao.updateCredit(machineId, coinValue);
		});
		
		mqttClient.subscribe(String.format(CashRegisterTopics.CASHREGISTER_CHECKCREDIT_RESPONSE, idMachine), (topic, msg) -> {
		    // Estrazione dell'ID della macchina dal topic
		    String[] topicLevels = topic.split("/");
		    String machineIdStr = topicLevels[2];
		    int machineId = Integer.parseInt(machineIdStr);

		    // Converte il payload a stringa
		    String payloadStringRec = new String(msg.getPayload(), StandardCharsets.UTF_8);
		    System.out.println(payloadStringRec);

		    // Converte la stringa in double
		    double credit = cashRegisterDao.getCredit(machineId);

		    String payloadString = String.valueOf(credit);
            byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);

            mqttClient.publish(String.format(CashRegisterTopics.CASHREGISTER_CURRENTCREDIT_RESPONSE, idMachine), payloadBytes);
		});
		
		mqttClient.subscribe(String.format(CashRegisterTopics.CASHREGISTER_REFUND_REQUEST, idMachine), (topic, msg) -> {
		    // Estrazione dell'ID della macchina dal topic
		    String[] topicLevels = topic.split("/");
		    String machineIdStr = topicLevels[2];
		    int machineId = Integer.parseInt(machineIdStr);

		    // Converte il payload a stringa
		    String payloadStringRec = new String(msg.getPayload(), StandardCharsets.UTF_8);
		    System.out.println(payloadStringRec);

		    // Converte la stringa in double
		    double credit = cashRegisterDao.getCredit(machineId);
		    cashRegisterDao.resetCredit(machineId);

		    String payloadString = String.valueOf(credit);
            byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);

            mqttClient.publish(String.format(CashRegisterTopics.CASHREGISTER_REFUND_RESPONSE, idMachine), payloadBytes);
		});
		
		mqttClient.subscribe(String.format(CashRegisterTopics.CASHREGISTER_CHECKCREDIT_REQUEST, idMachine), (topic, msg) -> {
		    // Ricavo l'ID macchina dal topic
		    String[] topicLevels = topic.split("/");
		    String machineIdStr = topicLevels[2];
		    int idMachine = Integer.parseInt(machineIdStr);

		    // Recupero dal payload il prezzo richiesto per la bevanda
		    String payloadStringRec = new String(msg.getPayload(), StandardCharsets.UTF_8);
		    double price = Double.parseDouble(payloadStringRec);
		    System.out.println("[CashboxManager] Verifica credito per price=" + price);

		    // Verifico e, se sufficiente, scalo il credito e aggiorno la cassa
		    boolean result = cashRegisterDao.payBeveragePrice(idMachine, price);
		 
		    if (result) {
		        // Pagamento OK, verifichiamo la cassa
		        coffeeMachine = cashRegisterDao.getMachineData(idMachine);

		        if (coffeeMachine.getTotalCash() >= (coffeeMachine.getMaxCash() - 5)) {
		            System.out.println("[CashboxManager] Raggiunta soglia, segnalo full_cash all'Assistance.");
		            byte[] payload = "full_cash".getBytes(StandardCharsets.UTF_8);
		            mqttClient.publish(
		                String.format(CashRegisterTopics.CASHREGISTER_STATUS, idMachine),
		                payload
		            );
		        }
		    }

		    // Preparo il payload di risposta
		    String response = result ? "OK" : "INSUFFICIENT_CREDIT";
		    byte[] payloadResp = response.getBytes(StandardCharsets.UTF_8);

		    // Pubblico la risposta al topic bevande
		    mqttClient.publish(String.format(CashRegisterTopics.CASHREGISTER_CHECKCREDIT_RESPONSE, idMachine), payloadResp);
		});

    }

}


/* Altro modo per fare la stessa roba cosa senza lambda function
    * 
    * mqttClient.subscribe("/cashboxService/" + idMachine + "/insert/request", new
    * IMqttMessageListener() {
    * 
    * @Override public void messageArrived(String topic, MqttMessage msg) throws
    * Exception { String[] topicLevels = topic.split("/"); String machineIdStr =
    * topicLevels[2]; int machineId = Integer.parseInt(machineIdStr);
    * 
    * String payloadString = new String(msg.getPayload(), StandardCharsets.UTF_8);
    * System.out.println(payloadString); // ... } });
*/