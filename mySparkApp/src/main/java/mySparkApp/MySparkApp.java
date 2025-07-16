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

import com.google.gson.Gson;

import mySparkApp.machine.ControllerCoffeeMachine;

public class MySparkApp {
	
	static Gson gson = new Gson();
	static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

		setup();
        //get("/login", (request, response) -> "");
        //get("/", (request, response) -> "root");
    }
    
	public static void setup() {

		String serverUrl = "ssl://localhost:8883";
    	String home = System.getProperty("user.home");
		String caFilePath = home + "/Documents/VisualStudioCodeProject/MachineCoffeeProject/mySparkApp/TLS/ca/ca.crt";
    	String clientCrtFilePath = home + "/Documents/VisualStudioCodeProject/MachineCoffeeProject/mySparkApp/TLS/client/client.crt";
    	String clientKeyFilePath = home + "/Documents/VisualStudioCodeProject/MachineCoffeeProject/mySparkApp/TLS/client/client.key";
		MqttClient client;
		String clientId = "ManagementService";

		try {
			client = new MqttClient(serverUrl, clientId);
			MqttConnectOptions options = new MqttConnectOptions();
			
			options.setConnectionTimeout(60);
			options.setKeepAliveInterval(60);
			options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

			
			SSLSocketFactory socketFactory = getSocketFactory(caFilePath, clientCrtFilePath, clientKeyFilePath, "");
			options.setSocketFactory(socketFactory);

			client.connect(options);

			client.disconnect();

		} catch (MqttException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    // Parte Database (usando DBConnect)
		try {
			DBConnect dbConnect = DBConnect.getInstance(); 
			Connection con = dbConnect.getConnection();
			//Dao.deleteCoffeeMachineDb(15);
			Dao.createCoffeeMachineDb(1);

			
			
			System.out.println("Operazioni sul DB completate!");
			con.close(); // Chiudi la connessione 
		} catch (Exception e) {
			System.out.println("Errore DB: " + e.getMessage());
		}
	}

	private static void setupRoutes() {
		path("/api/v1.0", () -> {

			//Add coffee machine
			post("/institute/:id_institute/machines", (req, res) -> {
				String json = req.body();
				System.out.println(json);

				int idInstitute = Integer.parseInt(req.params(":id_institute"));
				ControllerCoffeeMachine controller = new ControllerCoffeeMachine(idInstitute);
				//int idMachine = Dao.createNewCoffeeMachineDb(idInstitute);
				//controller.setIdMachine(idMachine);
				res.status(201);
				return gson.toJson(controller);
			});

			get("/login", (request, response) -> "");
			get("/", (request, response) -> "root");
		});
	}


}
