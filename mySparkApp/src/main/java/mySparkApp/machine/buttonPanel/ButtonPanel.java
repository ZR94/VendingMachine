package mySparkApp.machine.buttonPanel;

import static spark.Spark.options;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.google.gson.Gson;

import mySparkApp.machine.Beverage;
import mySparkApp.machine.utils.MqttModClient;

import org.eclipse.paho.client.mqttv3.MqttException;

public class ButtonPanel {

    private final Scanner scanner;
    private static MqttModClient mqttClient;
    private static ButtonPanelDao dao;
    public static int idMachine; //da controllare se funziona

    public ButtonPanel(int idM) {

        idMachine = idM;
		dao = new ButtonPanelDao();
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
		 
		String clientId = "Display Service: " + idMachine;

		try {
			mqttClient = new MqttModClient(serverUrl, caFilePath,clientCrtFilePath, clientKeyFilePath, mqttUserName, mqttPassword, clientId);
			mqttClient.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


    protected String handleInsertCoin(Double value) {

		String payloadString = String.valueOf(value);
		byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);

		try {
            mqttClient.publish(String.format(ButtonPanelTopics.BP_PAYMENT_REQ, idMachine), payloadBytes);
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		return "Not implemented";
	}

	protected List<Beverage> getAllBeverage(int idMachine) {

		List<Beverage> result = null;
		try {
			result = dao.getAllBeverages(idMachine);

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

		try {
            mqttClient.subscribe(String.format(ButtonPanelTopics.BP_CURRENTCREDIT_RES, idMachine), (topic, msg) -> {
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
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		try { // 2) Faccio il publish della richiesta
			String payloadString = "Richiesta credito corrente";
			byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);
			mqttClient.publish(String.format(ButtonPanelTopics.BP_CURRENTCREDIT_REQ, idMachine), payloadBytes);

			// 3) Blocco finché non arriva la risposta (o finché non scade un timeout
			latch.await(); // volendo: latch.await(3, TimeUnit.SECONDS);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MqttException e) {
            // TODO Auto-generated catch block
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

		try {
            mqttClient.subscribe(String.format(ButtonPanelTopics.BP_REFUND_CREDIT_RES, idMachine), (topic, msg) -> {
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
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		try { // 2) Faccio il publish della richiesta
			String payloadString = "Richiesta recupero credito";
			byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);
			mqttClient.publish(String.format(ButtonPanelTopics.BP_REFUND_CREDIT_REQ, idMachine), payloadBytes);

			// 3) Blocco finché non arriva la risposta (o finché non scade un timeout
			latch.await(); // volendo: latch.await(3, TimeUnit.SECONDS);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MqttException e) {
            // TODO Auto-generated catch block
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
		try {
            mqttClient.subscribe(String.format(ButtonPanelTopics.BP_CHOICE_RES, idMachine), (topic, msg) -> {
            	String resultString = new String(msg.getPayload(), StandardCharsets.UTF_8);
            	// Salvo l'esito nella variabile e conto-down per sbloccare il thread
            	resultContainer[0] = resultString;
            	latch.countDown();
            });
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		try {
			// Invio la richiesta di scelta bevanda, pubblicando l'ID della bevanda
			String payloadString = String.valueOf(choice);
			byte[] payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);
			mqttClient.publish(String.format(ButtonPanelTopics.BP_CHOICE_REQ, idMachine), payloadBytes);

			// Resto in attesa che arrivi la risposta o fino a timeout
			latch.await();

		} catch (InterruptedException e) {
			e.printStackTrace();
			return "ERRORE_INTERNO";
		} catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		// Ritorno l'esito finale della scelta bevanda
		//System.out.println("RESULT CONTROLLER: " + resultContainer[0]);
		return resultContainer[0];
	}
	
	// In DisplayController.java
	public String getMachineStatus() {
	    return dao.getMachineStatus(idMachine);
	}

}

/*
   public class ButtonPanel {
    private Dao queryManager; // Dao instance for database queries
    private double insertedMoney; // Variable to store the money inserted by the user
    private int selectedBeverageId; // Variable to store the selected beverage ID
    private int selectedSugarQuantity; // Variable to store the selected sugar quantity
    private int selectedPaperGlassQuantity; // Variable to store the selected paper glass quantity

    public ButtonPanel() {
        queryManager = new Dao();
        insertedMoney = 0.0;
    }

    public void selectBeverage(int beverageId) {
        selectedBeverageId = beverageId;
    }

    public void selectSugarQuantity(int sugarQuantity) {
        selectedSugarQuantity = sugarQuantity;
    }

    public void selectPaperGlassQuantity(int paperGlassQuantity) {
        selectedPaperGlassQuantity = paperGlassQuantity;
    }

    public void dispenseBeverage() {
        Map<Integer, Double> prices = queryManager.retrievePrices();
        if (insertedMoney >= prices.get(selectedBeverageId)) {
            if (queryManager.isBeverageAvailable(selectedBeverageId)) {
                if (queryManager.isSugarQuantityAvailable(selectedSugarQuantity)) {
                    if (queryManager.isPaperGlassQuantityAvailable(selectedPaperGlassQuantity)) {
                        // Dispense the beverage
                        System.out.println("Dispensing beverage " + selectedBeverageId);
                        insertedMoney -= prices.get(selectedBeverageId);
                        System.out.println("Remaining balance: " + insertedMoney);
                    } else {
                        System.out.println("Sorry, the selected paper glass quantity is not available.");
                    }
                } else {
                    System.out.println("Sorry, the selected sugar quantity is not available.");
                }
            } else {
                System.out.println("Sorry, the beverage is not available.");
            }
        } else {
            System.out.println("Insufficient balance. Please insert more money.");
        }
    }

    public void insertMoney(double amount) {
        insertedMoney += amount;
        System.out.println("Inserted money: " + amount);
        System.out.println("Total balance: " + insertedMoney);
    }

    public void refundMoney() {
        System.out.println("Refunding money: " + insertedMoney);
        insertedMoney = 0.0;
    }
}

 */