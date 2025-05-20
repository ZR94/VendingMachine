package mySparkApp.machine.dispenser;

public class DispenserTopics {

    final static String DEFAULT_TOPIC = "/machine/%d";

    //Publish
    String DISPENSER_STATUS = DEFAULT_TOPIC + " /support/statusUpdate/notify";

    String DISPENSER_CHOICE_RESPONSE = DEFAULT_TOPIC + " /dispenser/choice/response";
    
    String DISPENSER_CHECKCREDIT_REQUEST = DEFAULT_TOPIC + " /cashRegister/check/request";

    //Subscribe

    String DISPENSER_CHOICE_REQUEST = DEFAULT_TOPIC + " /dispenser/choice/request";

    String DISPENSER_CHECKCREDIT_RESPONSE = DEFAULT_TOPIC + " /cashRegister/check/response";


}
