package mySparkApp.machine.cashRegister;

public class CashRegisterTopics {

    final static String DEFAULT_TOPIC = "/machine/%d";

    //Publish
    String CASHREGISTER_STATUS = DEFAULT_TOPIC + " /support/statusUpdate/notify";

    String CASHREGISTER_CHANGE = DEFAULT_TOPIC + " /buttonPanel/change/response";

    String CASHREGISTER_CURRENTCREDIT_RESPONSE = DEFAULT_TOPIC + " /buttonPanel/credit/response";

    String CASHREGISTER_CHECKCREDIT_RESPONSE = DEFAULT_TOPIC + " /dispenser/check/response";

    //Subscribe
    String CASHREGISTER_PAYMENT = DEFAULT_TOPIC + " /cashRegister/payment/insert/request";

    String CASHREGISTER_REFUND = DEFAULT_TOPIC + " /cashRegister/refund/request";

    String CASHREGISTER_CURRENTCREDIT_REQUEST = DEFAULT_TOPIC + " /cashRegister/credit/request";
    
    String CASHREGISTER_CHECKCREDIT_REQUEST = DEFAULT_TOPIC + " /cashRegister/check/request";

}
