package mySparkApp.machine.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.eclipse.paho.client.mqttv3.*;

import mySparkApp.machine.SSLUtils;

public class MqttModClient {

    private final MqttClient client;
    private final MqttConnectOptions options;

    public MqttModClient(String brokerUrl, String clientId, String username, String password,
                      String caFile, String certFile, String keyFile) throws Exception {

        this.client = new MqttClient(brokerUrl, clientId);
        this.options = new MqttConnectOptions();

        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(60);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setSocketFactory(SSLUtils.createSocketFactory(caFile, certFile, keyFile, password));
    }

    public void connect() throws MqttException {
        client.connect(options);
        System.out.println("Connected.");
    }

    public void disconnect() throws MqttException {
        client.disconnect();
        System.out.println("Disconnected.");
    }

    public void publish(String topic, byte[] payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload);
        client.publish(topic, message);
        System.out.println("Published: " + topic + " - " + message);
    }

    public void subscribe(String topic, IMqttMessageListener listener) throws MqttException {
        client.subscribe(topic, listener);
        System.out.println("Subscribed to: " + topic);
    }
}
