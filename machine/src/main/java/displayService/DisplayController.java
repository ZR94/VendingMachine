package displayService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import models.Beverage;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import utils.MQTTClient;

public class DisplayController {

	private final Scanner scanner;
	public static int machine_id;
	private static MQTTClient mqttClient;
	private static DisplayDao dao;

	public DisplayController(int machineID) {
		machine_id = machineID;
		dao = new DisplayDao();

		this.scanner = new Scanner(System.in);
	}

	protected void setupMQTT() {
		// Configurazione del client MQTT
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
        
        String mqttUserName = "display"; String mqttPassword = "display";
		 
		String clientId = "Display Service: " + machine_id;

		mqttClient = new MQTTClient(serverUrl, caFilePath,clientCrtFilePath, clientKeyFilePath, mqttUserName, mqttPassword, clientId);
		mqttClient.connect();
	}

	protected String handleInsertCoin(Double value) {

		String payloadString = String.valueOf(value);
		byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);

		mqttClient.publish(String.format(TopicsDisplay.DISPLAY_INSERT_COIN_REQ, machine_id), payloadBytes);

		return "Not implemented";
	}

	protected List<Beverage> getAllBeverage() {

		List<Beverage> result = null;
		try {
			result = dao.getAllDrink();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	protected Double getCurrentCredit() {

		// Variabile container per il valore
		final Double[] resultContainer = new Double[1];
		// Latch per sincronizzare
		CountDownLatch latch = new CountDownLatch(1);

		mqttClient.subscribe(String.format(TopicsDisplay.DISPLAY_CURR_CREDIT_RES, machine_id), (topic, msg) -> {
			String creditString = new String(msg.getPayload(), StandardCharsets.UTF_8);
			try {
				double credit = Double.parseDouble(creditString);
				resultContainer[0] = credit;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				resultContainer[0] = null;
			} finally {
				// Segnalo che ho finito di ricevere ed elaborare il messaggio
				latch.countDown();
			}
		});

		try { // 2) Faccio il publish della richiesta
			String payloadString = "Richiesta credito corrente";
			byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);
			mqttClient.publish(String.format(TopicsDisplay.DISPLAY_CURR_CREDIT_REQ, machine_id), payloadBytes);

			// 3) Blocco finché non arriva la risposta (o finché non scade un timeout
			latch.await(); // volendo: latch.await(3, TimeUnit.SECONDS);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 4) Ritorno il valore (attenzione a eventuale null)
		return resultContainer[0];
	}

	protected Double resetCurrCredit() {
		// Variabile container per il valore
		final Double[] resultContainer = new Double[1];
		// Latch per sincronizzare
		CountDownLatch latch = new CountDownLatch(1);

		mqttClient.subscribe(String.format(TopicsDisplay.DISPLAY_RETURN_CREDIT_RES, machine_id), (topic, msg) -> {
			String creditString = new String(msg.getPayload(), StandardCharsets.UTF_8);
			try {
				double credit = Double.parseDouble(creditString);
				resultContainer[0] = credit;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				resultContainer[0] = null;
			} finally {
				// Segnalo che ho finito di ricevere ed elaborare il messaggio
				latch.countDown();
			}
		});

		try { // 2) Faccio il publish della richiesta
			String payloadString = "Richiesta recupero credito";
			byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);
			mqttClient.publish(String.format(TopicsDisplay.DISPLAY_RETURN_CREDIT_REQ, machine_id), payloadBytes);

			// 3) Blocco finché non arriva la risposta (o finché non scade un timeout
			latch.await(); // volendo: latch.await(3, TimeUnit.SECONDS);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 4) Ritorno il valore (attenzione a eventuale null)
		return resultContainer[0];
	}

	protected String handleBeverageChoice(int choice) {

		// Variabile container per il valore di ritorno
		final String[] resultContainer = new String[1];
		// Latch per sincronizzare la ricezione del messaggio
		CountDownLatch latch = new CountDownLatch(1);

		// Mi metto in ascolto sul topic di risposta
		mqttClient.subscribe(String.format(TopicsDisplay.DISPLAY_CHOICE_BEV_RES, machine_id), (topic, msg) -> {
			String resultString = new String(msg.getPayload(), StandardCharsets.UTF_8);
			// Salvo l'esito nella variabile e conto-down per sbloccare il thread
			resultContainer[0] = resultString;
			latch.countDown();
		});

		try {
			// Invio la richiesta di scelta bevanda, pubblicando l'ID della bevanda
			String payloadString = String.valueOf(choice);
			byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);
			mqttClient.publish(String.format(TopicsDisplay.DISPLAY_CHOICE_BEV_REQ, machine_id), payloadBytes);

			// Resto in attesa che arrivi la risposta o fino a timeout
			latch.await();

		} catch (InterruptedException e) {
			e.printStackTrace();
			return "ERRORE_INTERNO";
		}

		// Ritorno l'esito finale della scelta bevanda
		//System.out.println("RESULT CONTROLLER: " + resultContainer[0]);
		return resultContainer[0];
	}
	
	// In DisplayController.java
	public String getMachineStatus() {
	    return dao.getMachineStatus(machine_id);
	}

}
