package mySparkApp.machine.buttonPanel;

import static spark.Spark.options;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

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

import org.eclipse.paho.client.mqttv3.MqttException;

public class ButtonPanel {

    static Gson gson = new Gson();
    String serverUrl = "ssl://localhost:8883";
    private MqttClient mqttClient;
    private ButtonPanelDao buttonPanelDao;
    MqttClient client;

    public ButtonPanel() throws MqttException {

        mqttClient = new MqttClient(serverUrl, "ButtonPanel");

    }

    protected void setupMQTT() {
		// Configurazione del client MQTT
		// da impostare poi con ssl al posto di tcp quando si implementa connessione TLS
        client = new MqttClient(serverUrl, "ButtonPanel" + machine_id);
        MqttConnectOptions options = new MqttConnectOptions();
		
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

        SSLSocketFactory socketFactory = getSocketFactory(caFilePath, clientCrtFilePath, clientKeyFilePath, mqttPassword);
		options.setSocketFactory(socketFactory);
		
		mqttClient.connect();
	}

    private static SSLSocketFactory getSocketFactory(final String caCrtFile,
			final String crtFile, final String keyFile, final String password)
			throws Exception {
		Security.addProvider(new BouncyCastleProvider());

		// load CA certificate
		X509Certificate caCert = null;

		FileInputStream fis = new FileInputStream(caCrtFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		while (bis.available() > 0) {
			caCert = (X509Certificate) cf.generateCertificate(bis);
			// System.out.println(caCert.toString());
		}

		// load client certificate
		bis = new BufferedInputStream(new FileInputStream(crtFile));
		X509Certificate cert = null;
		while (bis.available() > 0) {
			cert = (X509Certificate) cf.generateCertificate(bis);
			// System.out.println(caCert.toString());
		}

		// load client private key
		PEMParser pemParser = new PEMParser(new FileReader(keyFile));
		Object object = pemParser.readObject();
		PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder()
				.build(password.toCharArray());
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter()
				.setProvider("BC");
		KeyPair key;
		if (object instanceof PEMEncryptedKeyPair) {
			System.out.println("Encrypted key - we will use provided password");
			key = converter.getKeyPair(((PEMEncryptedKeyPair) object)
					.decryptKeyPair(decProv));
		} else if (object instanceof PrivateKeyInfo) {
			System.out.println("Unencrypted PrivateKeyInfo key - no password needed");
			key = converter.getKeyPair(convertPrivateKeyFromPKCS8ToPKCS1((PrivateKeyInfo)object));
		} else {
			System.out.println("Unencrypted key - no password needed");
			key = converter.getKeyPair((PEMKeyPair) object);
		}
		pemParser.close();

		// CA certificate is used to authenticate server
		KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
		caKs.load(null, null);
		caKs.setCertificateEntry("ca-certificate", caCert);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
		tmf.init(caKs);

		// client key and certificates are sent to server so it can authenticate us
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, null);
		ks.setCertificateEntry("certificate", cert);
		ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
				new java.security.cert.Certificate[] { cert });
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
				.getDefaultAlgorithm());
		kmf.init(ks, password.toCharArray());

		// finally, create SSL socket factory
		SSLContext context = SSLContext.getInstance("TLSv1.2");
		context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		return context.getSocketFactory();
	}
    
	private static PEMKeyPair convertPrivateKeyFromPKCS8ToPKCS1(PrivateKeyInfo privateKeyInfo) throws Exception {

/* 

    public void selectBeverage(String beverage) {
        // Controlla se la macchina è in stato guasto
        if (isMachineFaulty()) {
            // Comunica alla classe Support che la macchina è guasta
            sendAlertToSupport("Macchina guasta");
            return;
        }

        // Controlla se le capsule per fare le bevande, i bicchierini e lo zucchero richiesto sono disponibili
        if (!isDispenserAvailable(beverage)) {
            System.out.println("Dispenser non disponibile");
            return;
        }

        // Controlla se la cassa non ha raggiunto la capienza massima
        if (!isCashRegisterAvailable()) {
            System.out.println("Cassa piena");
            return;
        }

        // Accetta i soldi e depositali in cassa
        acceptMoney(beverage);
    }

    private boolean isMachineFaulty() {
        // Legge lo stato della macchina da un topic MQTT
        MqttMessage message = mqttClient.getMessage("machine/status");
        return message.getPayload().equals("guasto");
    }

    private boolean isDispenserAvailable(String beverage) {
        // Pubblica un messaggio MQTT per chiedere lo stato del dispenser
        mqttClient.publish(topicDispenser, new MqttMessage(beverage.getBytes()));
        // Legge la risposta del dispenser da un topic MQTT
        MqttMessage message = mqttClient.getMessage(topicDispenser + "/response");
        return message.getPayload().equals("disponibile");
    }

    private boolean isCashRegisterAvailable() {
        // Pubblica un messaggio MQTT per chiedere lo stato della cassa
        mqttClient.publish(topicCashRegister, new MqttMessage());
        // Legge la risposta della cassa da un topic MQTT
        MqttMessage message = mqttClient.getMessage(topicCashRegister + "/response");
        return message.getPayload().equals("disponibile");
    }

    private void acceptMoney(String beverage) {
        // Accetta i soldi e depositali in cassa
        System.out.println("Soldi accettati");
        // Pubblica un messaggio MQTT per confermare l'accettazione dei soldi
        try {
            mqttClient.publish(topicCashRegister + "/confirm", new MqttMessage(beverage.getBytes()));
        } catch (MqttPersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void sendAlertToSupport(String message) {
        // Pubblica un messaggio MQTT per avvisare la classe Support
        try {
            mqttClient.publish(topicSupport, new MqttMessage(message.getBytes()));
        } catch (MqttPersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
*/

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