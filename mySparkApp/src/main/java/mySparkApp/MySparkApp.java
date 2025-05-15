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
import java.sql.Statement;

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

public class MySparkApp {
	

    public static void main(String[] args) {

		Gson gson = new Gson();
    	String serverUrl = "ssl://localhost:8883";
    	String home = System.getProperty("user.home");
		String caFilePath = home + "/Documents/VisualStudioCodeProject/MachineCoffeeProject/mySparkApp/TLS/ca/ca.crt";
    	String clientCrtFilePath = home + "/Documents/VisualStudioCodeProject/MachineCoffeeProject/mySparkApp/TLS/client/client.crt";
    	String clientKeyFilePath = home + "/Documents/VisualStudioCodeProject/MachineCoffeeProject/mySparkApp/TLS/client/client.key"; 

    	
		MqttClient client;
		try {
			client = new MqttClient(serverUrl, "2");
			MqttConnectOptions options = new MqttConnectOptions();
			
			options.setConnectionTimeout(60);
			options.setKeepAliveInterval(60);
			options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

			
			SSLSocketFactory socketFactory = getSocketFactory(caFilePath, clientCrtFilePath, clientKeyFilePath, "");
			options.setSocketFactory(socketFactory);

			System.out.println("starting connect the server...");
			client.connect(options);
			System.out.println("connected!");
			Thread.sleep(1000);
			String TOPIC = "/pissir/prova";
			MqttTopic timeTopic = client.getTopic(TOPIC);
			String body = "ciao a tutti";
			timeTopic.publish(new MqttMessage(body.getBytes()));
			System.out.println("Published: " + body + " on topic: " + TOPIC);
			Thread.sleep(5000);

			client.disconnect();
			System.out.println("disconnected!");


		} catch (MqttException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    // Parte Database (usando DBConnect)
    try {
        DBConnect dbConnect = DBConnect.getInstance(); 
        Connection con = dbConnect.getConnection();   
        
        
        System.out.println("Operazioni sul DB completate!");
        con.close(); // Chiudi la connessione 
    } catch (Exception e) {
        System.out.println("Errore DB: " + e.getMessage());
    }
		
		
        //get("/login", (request, response) -> "");
        //get("/", (request, response) -> "root");
    }
    
    //gruppo di classi che ci forniscono delle funzioni in maniera facile
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
		  // Parse the key wrapping to determine the internal key structure
		  ASN1Encodable asn1PrivateKey = privateKeyInfo.parsePrivateKey();
		  // Convert the parsed key to an RSA private key
		  RSAPrivateKey keyStruct = RSAPrivateKey.getInstance(asn1PrivateKey);
		  // Create the RSA public key from the modulus and exponent
		  RSAPublicKey pubSpec = new RSAPublicKey(
		    keyStruct.getModulus(), keyStruct.getPublicExponent());
		  // Create an algorithm identifier for forming the key pair
		  AlgorithmIdentifier algId = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
		    System.out.println("Converted private key from PKCS #8 to PKCS #1 RSA private key\n");
		  // Create the key pair container
		  return new PEMKeyPair(new SubjectPublicKeyInfo(algId, pubSpec), new PrivateKeyInfo(algId, keyStruct));
	}

}
