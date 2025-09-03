package beverageService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import com.google.gson.Gson;

import utils.MQTTClient;

public class BeverageManager {

	static Gson gson = new Gson();
	private static int machine_id = 100;
	private static MQTTClient mqttClient;
	private static BeverageDao dao;
	private static int idBeverageSelected;

	public static void main(String[] args) {
		System.out.println("***Beverage Manager Console ID: " + machine_id + " ***");
		dao = new BeverageDao();
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
       
        String mqttUserName = "beverage"; String mqttPassword = "beverage";
		 
		String clientId = "Beverage Service :" + machine_id;

		mqttClient = new MQTTClient(serverUrl, caFilePath, clientCrtFilePath, clientKeyFilePath, mqttUserName, mqttPassword, clientId);
		mqttClient.connect();
		
	       // 1) Sottoscrivo la richiesta di scelta bevanda
        mqttClient.subscribe(String.format(TopicsBeverage.BEVERAGE_CHOICE_BEV_REQ, machine_id), (topic, msg) -> {
            String payloadStringRec = new String(msg.getPayload(), StandardCharsets.UTF_8);
            int beverageId = Integer.parseInt(payloadStringRec);
            idBeverageSelected = beverageId;
            

            System.out.println("[BeverageManager] Bevanda scelta: " + beverageId);

            // Verifico se le cialde sono disponibili
            boolean enoughPods = dao.checkPodsForBeverage(beverageId);
            if (!enoughPods) {
                // Rispondo direttamente al display con "NOT_ENOUGH_PODS"
                byte[] payloadResp = "NOT_ENOUGH_PODS".getBytes(StandardCharsets.UTF_8);
                mqttClient.publish(String.format(TopicsBeverage.BEVERAGE_CHOICE_BEV_RES, machine_id), payloadResp);
            } else {
                // Se ci sono abbastanza cialde, prendo il prezzo bevanda
                double price = dao.getBeveragePrice(beverageId);

                // Invio richiesta al cashboxService per verificare il credito
                // (payload = prezzo bevanda)
                String priceStr = String.valueOf(price);
                byte[] payloadPrice = priceStr.getBytes(StandardCharsets.UTF_8);
                mqttClient.publish(String.format(TopicsBeverage.BEVERAGE_VERIFY_CREDIT_REQ, machine_id), payloadPrice);

                // Mi aspetto una risposta su "BEVERAGE_VERIFY_CREDIT_RES"
                // (vedi la subscribe più in basso)
            }
        });

        // 2) Sottoscrivo la risposta del cashboxService per la verifica credito
        mqttClient.subscribe(String.format(TopicsBeverage.BEVERAGE_VERIFY_CREDIT_RES, machine_id), (topic, msg) -> {
            String resultCreditCheck = new String(msg.getPayload(), StandardCharsets.UTF_8);
            // Il cashboxService può mandare "OK" oppure "INSUFFICIENT_CREDIT"
            System.out.println("[BeverageManager] Verifica credito: " + resultCreditCheck);

            if ("OK".equals(resultCreditCheck)) {
                int lastBeverageId = idBeverageSelected; // recupero in qualche modo
                dao.deductPodsForBeverage(lastBeverageId);
                
                if (dao.isAnyPodLow()) {
                    System.out.println("[BeverageManager] Rilevato numero cialde <= 5, segnalo low_pods all'Assistance.");

                    byte[] payload = "low_pods".getBytes(StandardCharsets.UTF_8);
                    mqttClient.publish(
                        String.format(TopicsBeverage.BEVERAGE_STATUS_NOTIFY, machine_id), 
                        payload
                    );
                }

                // Erogazione riuscita
                byte[] payloadResp = "OK".getBytes(StandardCharsets.UTF_8);
                mqttClient.publish(String.format(TopicsBeverage.BEVERAGE_CHOICE_BEV_RES, machine_id), payloadResp);

            } else {
                // Credito insufficiente
                byte[] payloadResp = "INSUFFICIENT_CREDIT".getBytes(StandardCharsets.UTF_8);
                mqttClient.publish(String.format(TopicsBeverage.BEVERAGE_CHOICE_BEV_RES, machine_id), payloadResp);
            }
        });
    }


}
