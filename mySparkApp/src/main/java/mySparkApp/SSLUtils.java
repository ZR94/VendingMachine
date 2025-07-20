package mySparkApp;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class SSLUtils {

    public static SSLSocketFactory createSocketFactory(String caFile, String certFile, String keyFile, String password) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // Load CA
        X509Certificate caCert = loadCertificate(caFile);
        X509Certificate clientCert = loadCertificate(certFile);
        PrivateKey clientKey = loadPrivateKey(keyFile);

        // Trust Store
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null);
        trustStore.setCertificateEntry("caCert", caCert);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // Key Store
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        keyStore.setCertificateEntry("cert", clientCert);
        keyStore.setKeyEntry("key", clientKey, password.toCharArray(), new java.security.cert.Certificate[]{clientCert});

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, password.toCharArray());

        // SSL context
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        return sslContext.getSocketFactory();
    }

    private static X509Certificate loadCertificate(String filePath) throws Exception {
        try (InputStream in = new BufferedInputStream(new FileInputStream(filePath))) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(in);
        }
    }

    private static PrivateKey loadPrivateKey(String filePath) throws Exception {
        try (PEMParser pemParser = new PEMParser(new FileReader(filePath))) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            if (object instanceof PEMKeyPair) {
                return converter.getKeyPair((PEMKeyPair) object).getPrivate();
            }

            throw new IllegalArgumentException("Unsupported private key format");
        }
    }
}
