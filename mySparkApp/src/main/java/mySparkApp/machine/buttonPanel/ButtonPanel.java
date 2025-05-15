package mySparkApp.machine.buttonPanel;

import java.util.Map;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttException;

public class ButtonPanel {

    static Gson gson = new Gson();
    String serverUrl = "ssl://localhost:8883";
    private MqttClient mqttClient;
    private ButtonPanelDao buttonPanelDao;

    public ButtonPanel() throws MqttException {
        mqttClient = new MqttClient(serverUrl, "ButtonPanel");

    }

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