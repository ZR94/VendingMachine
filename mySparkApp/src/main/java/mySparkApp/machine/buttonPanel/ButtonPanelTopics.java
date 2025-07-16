package mySparkApp.machine.buttonPanel;

public class ButtonPanelTopics {

    final static String DEFAULT_TOPIC = "/machine/%d";
	
	// TOPIC PUBLISH
    protected static final String BP_PAYMENT_REQ = DEFAULT_TOPIC + "/cashRegister/payment/insert/request";
    protected static final String BP_CURRENTCREDIT_REQ = DEFAULT_TOPIC + "/cashRegister/credit/request";
    protected static final String BP_REFUND_CREDIT_REQ = DEFAULT_TOPIC + "/cashRegister/refund/request";
    
    protected static final String BP_CHOICE_REQ = DEFAULT_TOPIC + "/dispenser/choice/request";
    
    // TOPIC SUBSCRIBE
    protected static final String BP_CURRENTCREDIT_RES = DEFAULT_TOPIC +"/buttonPanel/credit/response";
    protected static final String BP_REFUND_CREDIT_RES = DEFAULT_TOPIC + "/buttonPanel/returnCredit/response";
    
    protected static final String BP_CHOICE_RES = DEFAULT_TOPIC + "/dispenser/choice/response";

}
