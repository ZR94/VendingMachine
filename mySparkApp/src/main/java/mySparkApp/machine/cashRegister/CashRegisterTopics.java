package mySparkApp.machine.cashRegister;

public class CashRegisterTopics {

    final static String DEFAULT_TOPIC = "/machine/%d";

    //Publish
    protected static final String CASHREGISTER_STATUS = DEFAULT_TOPIC + " /support/statusUpdate/notify";

    protected static final String CASHREGISTER_REFUND_RESPONSE = DEFAULT_TOPIC + " /buttonPanel/refund/response";

    protected static final String CASHREGISTER_CURRENTCREDIT_RESPONSE = DEFAULT_TOPIC + " /buttonPanel/credit/response";

    protected static final String CASHREGISTER_CHECKCREDIT_RESPONSE = DEFAULT_TOPIC + " /dispenser/check/response";

    //Subscribe
    protected static final String CASHREGISTER_PAYMENT = DEFAULT_TOPIC + " /cashRegister/payment/insert/request";

    protected static final String CASHREGISTER_REFUND_REQUEST = DEFAULT_TOPIC + " /cashRegister/refund/request";

    protected static final String CASHREGISTER_CURRENTCREDIT_REQUEST = DEFAULT_TOPIC + " /cashRegister/credit/request";
    
    protected static final String CASHREGISTER_CHECKCREDIT_REQUEST = DEFAULT_TOPIC + " /cashRegister/check/request";

}
