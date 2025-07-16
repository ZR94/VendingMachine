package mySparkApp.machine.support;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;

import mySparkApp.machine.Status;
import mySparkApp.machine.support.SupportDao;
import mySparkApp.machine.utils.MqttModClient;

public class Support {

    static Gson gson = new Gson();
    String serverUrl = "ssl://localhost:8883";
    private static MqttModClient mqttClient;
    private static SupportDao supportDao;
    private static int machine_id;

    public static <mqttClient> void setupMQTT() throws Exception {
        // MQTT Client Configuration
        // da impostare poi con ssl al posto di tcp quando si implementa connessione TLS
        String serverUrl = "ssl://localhost:8883";

        String caFilePath = Paths.get("").toAbsolutePath().resolve("certs").resolve("ca").resolve("ca.crt").toString();

        String clientCrtFilePath = Paths.get("").toAbsolutePath().resolve("certs").resolve("client")
                .resolve("client.crt").toString();

        String clientKeyFilePath = Paths.get("").toAbsolutePath().resolve("certs").resolve("client")
                .resolve("client.key").toString();

        String mqttUserName = "assistance";
        String mqttPassword = "assistance";

        String clientId = "Assistance Service :" + machine_id;

        mqttClient = new MqttModClient(serverUrl, clientId, mqttUserName, mqttPassword, caFilePath, clientCrtFilePath, clientKeyFilePath);
        mqttClient.connect();

        // 1) Sottoscrizione al topic di update/notify
        mqttClient.subscribe(String.format(SupportTopics.SUPPORT_STATUS_NOTIFY, machine_id), (topic, msg) -> {
            String payloadString = new String(msg.getPayload());
            System.out.println("[AssistanceManager] Ricevuto stato di errore: " + payloadString);

            String[] topicLevels = topic.split("/");
            String machineIdStr = topicLevels[2];
            int machineId = Integer.parseInt(machineIdStr);

            // 3) Aggiorniamo lo stato nel DB (es. 'faulty','low_pods','full_cash', 'active'
            supportDao.updateMachineStatus(machineId, payloadString);

            // aggiorno il management
            byte[] payloadResp = payloadString.getBytes(StandardCharsets.UTF_8);
            mqttClient.publish(String.format(SupportTopics.SUPPORT_STATUS_SEND, machine_id), payloadResp);
        });

        // 1) Sottoscrizione al topic di receive dal management
        mqttClient.subscribe(String.format(SupportTopics.SUPPORT_STATUS_RECEIVE, machine_id), (topic, msg) -> {
            String payloadString = new String(msg.getPayload());
            System.out.println("[AssistanceManager] Ricevuto tecnico dal management: " + payloadString);

            String[] topicLevels = topic.split("/");
            String machineIdStr = topicLevels[2];
            int machineId = Integer.parseInt(machineIdStr);

            // dao.refillPods();
            // dao.updateMachineStatus(machineId, Status.ACTIVE.getValue());
            // Ora provo a implementare (Mirko)
            if (machineId == machine_id) {
                switch (supportDao.getMachineStatus(machineId)) {
                    case FAULTY: {
                        supportDao.updateMachineStatus(machineId, Status.ACTIVE.getValue());
                        byte[] payloadResp = Status.ACTIVE.getValue().getBytes(StandardCharsets.UTF_8);
                        mqttClient.publish(String.format(SupportTopics.SUPPORT_STATUS_SEND, machine_id),
                                payloadResp);
                    }
                    case LOW_PODS: {
                        supportDao.refillPods();
                        supportDao.updateMachineStatus(machineId, Status.ACTIVE.getValue());
                        byte[] payloadResp = Status.ACTIVE.getValue().getBytes(StandardCharsets.UTF_8);
                        mqttClient.publish(String.format(SupportTopics.SUPPORT_STATUS_SEND, machine_id),
                                payloadResp);
                    }
                    case FULL_CASH: {
                        Double cashRemoved = supportDao.emptyCash(machineId);
                        byte[] cashSent = cashRemoved.toString().getBytes(StandardCharsets.UTF_8);
                        mqttClient.publish(String.format(SupportTopics.SUPPORT_STATUS_WITHDRAW_RES, machine_id),
                                cashSent);
                        supportDao.updateMachineStatus(machineId, Status.ACTIVE.getValue());
                        byte[] payloadResp = Status.ACTIVE.getValue().getBytes(StandardCharsets.UTF_8);
                        mqttClient.publish(String.format(SupportTopics.SUPPORT_STATUS_SEND, machine_id),
                                payloadResp);
                    }
                    default:
                        ;
                }
            }

        });

    }

}
