package mySparkApp;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Dispenser {
    String serverUrl = "ssl://localhost:8883";
    private MqttClient mqttClient;

    public Dispenser() {
        try {
            mqttClient = new MqttClient(serverUrl, "dispenser-client");
            mqttClient.connect();
            mqttClient.subscribe("your/topic");
        } catch (MqttException e) {
            System.out.println("Error connecting to MQTT broker: " + e.getMessage());
        }
    }

    public void receiveMessage(String topic, MqttMessage message) {
        System.out.println("Received message on topic " + topic + ": " + new String(message.getPayload()));
    }
}