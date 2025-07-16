package mySparkApp.machine.dispenser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

import mySparkApp.DBConnect;
import mySparkApp.Dao;
import mySparkApp.machine.dispenser.DispenserDao;
import mySparkApp.machine.cashRegister.CashRegisterTopics;
import mySparkApp.machine.support.SupportDao;
import mySparkApp.machine.utils.MqttModClient;

public class Dispenser {

    private Gson gson = new Gson();
    private static int machine_id = 100;
    private static MqttModClient mqttClient;
    private static DispenserDao dispenserDao;
    private static int idBeverageSelected;
    

    public static void main(String[] args) {
		System.out.println("***Beverage Manager Console ID: " + machine_id + " ***");
		dispenserDao = new DispenserDao();
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
       
        String mqttUserName = "beverage"; String mqttPassword = "beverage";
		 
		String clientId = "Beverage Service :" + machine_id;

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

        // 1) Sottoscrivo la richiesta di scelta bevanda
        mqttClient.subscribe(String.format(DispenserTopics.DISPENSER_CHOICE_REQUEST, machine_id), (topic, msg) -> {
            String payloadStringRec = new String(msg.getPayload(), StandardCharsets.UTF_8);
            int idBeverage = Integer.parseInt(payloadStringRec);
            idBeverageSelected = idBeverage;
            

            System.out.println("[BeverageManager] Bevanda scelta: " + idBeverage);

            // Verifico se le cialde sono disponibili
            boolean enoughPods = dispenserDao.checkPods(machine_id,idBeverage);
            if (!enoughPods) {
                // Rispondo direttamente al display con "NOT_ENOUGH_PODS"
                byte[] payloadResp = "NOT_ENOUGH_PODS".getBytes(StandardCharsets.UTF_8);
                mqttClient.publish(String.format(DispenserTopics.DISPENSER_CHOICE_RESPONSE, machine_id), payloadResp);
            } else {
                // Se ci sono abbastanza cialde, prendo il prezzo bevanda
                double price = dispenserDao.getBeveragePrice(machine_id, idBeverage);

                // Invio richiesta al cashboxService per verificare il credito
                // (payload = prezzo bevanda)
                String priceStr = String.valueOf(price);
                byte[] payloadPrice = priceStr.getBytes(StandardCharsets.UTF_8);
                mqttClient.publish(String.format(DispenserTopics.DISPENSER_CHECKCREDIT_REQUEST, machine_id), payloadPrice);

                // Mi aspetto una risposta su "BEVERAGE_VERIFY_CREDIT_RES"
                // (vedi la subscribe più in basso)
            }
        });

        // 2) Sottoscrivo la risposta del cashboxService per la verifica credito
        mqttClient.subscribe(String.format(DispenserTopics.DISPENSER_CHECKCREDIT_RESPONSE, machine_id), (topic, msg) -> {
            String resultCreditCheck = new String(msg.getPayload(), StandardCharsets.UTF_8);
            // Il cashboxService può mandare "OK" oppure "INSUFFICIENT_CREDIT"
            System.out.println("[BeverageManager] Verifica credito: " + resultCreditCheck);

            if ("OK".equals(resultCreditCheck)) {
                int lastidBeverage = idBeverageSelected; // recupero in qualche modo
                dispenserDao.deductPodsForBeverage(machine_id,lastidBeverage);
                
                if (dispenserDao.isAnyPodLow()) {
                    System.out.println("[BeverageManager] Rilevato numero cialde <= 5, segnalo low_pods all'Assistance.");

                    byte[] payload = "low_pods".getBytes(StandardCharsets.UTF_8);
                    mqttClient.publish(
                        String.format(DispenserTopics.DISPENSER_STATUS, machine_id), 
                        payload
                    );
                }

                // Erogazione riuscita
                byte[] payloadResp = "OK".getBytes(StandardCharsets.UTF_8);
                mqttClient.publish(String.format(DispenserTopics.DISPENSER_CHOICE_RESPONSE, machine_id), payloadResp);

            } else {
                // Credito insufficiente
                byte[] payloadResp = "INSUFFICIENT_CREDIT".getBytes(StandardCharsets.UTF_8);
                mqttClient.publish(String.format(DispenserTopics.DISPENSER_CHOICE_REQUEST, machine_id), payloadResp);
            }
        });

    }

}