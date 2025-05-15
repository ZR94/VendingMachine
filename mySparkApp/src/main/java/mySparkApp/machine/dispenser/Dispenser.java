package mySparkApp.machine.dispenser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

import mySparkApp.DBConnect;
import mySparkApp.Dao;
import mySparkApp.machine.support.SupportDao;

public class Dispenser implements MqttCallback{

    private Gson gson = new Gson();
    String serverUrl = "ssl://localhost:8883";
    private MqttClient mqttClient;
    private DispenserDao dispenserDao;

    public Dispenser() {
        try {
            mqttClient = new MqttClient(serverUrl, "dispenser-client");
            mqttClient.setCallback(this);
            mqttClient.connect();

            mqttClient.subscribe("id/sensors/cash");
        } catch (MqttException e) {
            System.out.println("Error connecting to MQTT broker: " + e.getMessage());
        }
    }

    public void receiveMessage(String topic, MqttMessage message) {
        System.out.println("Received message on topic " + topic + ": " + new String(message.getPayload()));
    }

    @Override
    public void connectionLost(Throwable cause) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connectionLost'");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        
    }



    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deliveryComplete'");
    }

}