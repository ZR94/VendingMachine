package mySparkApp;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class CashRegister {

    String serverUrl = "ssl://localhost:8883";
    private MqttClient mqttClient;

    public CashRegister(){

        try {
            mqttClient = new MqttClient(serverUrl, "dispenser-client");
            mqttClient.connect();
            mqttClient.subscribe("your/topic");
        } catch (MqttException e) {
            System.out.println("Error connecting to MQTT broker: " + e.getMessage());
        }
    }

}
