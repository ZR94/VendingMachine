package mySparkApp.machine.support;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

public class Support {

    static Gson gson = new Gson();
    String serverUrl = "ssl://localhost:8883";
    private MqttClient mqttClient;
    private SupportDao supportDao;

    public Support() {
        try {
            mqttClient = new MqttClient(serverUrl, "support-client");
            mqttClient.connect();
        } catch (MqttException e) {
            System.out.println("Error connecting to MQTT broker: " + e.getMessage());
        }
    }

    public void sendMessage(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            System.out.println("Error publishing message to MQTT broker: " + e.getMessage());
        }
    }
}
