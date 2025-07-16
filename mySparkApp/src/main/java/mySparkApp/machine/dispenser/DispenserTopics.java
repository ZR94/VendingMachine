package mySparkApp.machine.dispenser;

public class DispenserTopics {

    final static String DEFAULT_TOPIC = "/machine/%d";

    //Publish
    protected static final String DISPENSER_STATUS = DEFAULT_TOPIC + " /support/statusUpdate/notify";

    protected static final String DISPENSER_CHOICE_RESPONSE = DEFAULT_TOPIC + " /dispenser/choice/response";
    
    protected static final String DISPENSER_CHECKCREDIT_REQUEST = DEFAULT_TOPIC + " /cashRegister/check/request";

    //Subscribe

    protected static final String DISPENSER_CHOICE_REQUEST = DEFAULT_TOPIC + " /dispenser/choice/request";

    protected static final String DISPENSER_CHECKCREDIT_RESPONSE = DEFAULT_TOPIC + " /cashRegister/check/response";

}
