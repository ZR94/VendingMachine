package mySparkApp.machine.cashRegister;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.Gson;

public class CashRegister {

    private Gson gson = new Gson();
    String serverUrl = "ssl://localhost:8883";
    private MqttClient mqttClient;
    private CashRegisterDao cashRegisterDao;

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
