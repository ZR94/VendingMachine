package mySparkApp.machine.cashRegister;

public class CashRegisterTopics {

    final static String DEFAULT_TOPIC = "/machine/%d";

    //Publish
    String CashRegister_Status = DEFAULT_TOPIC + " /support/statusUpdate/notify";

    //Subscribe
    String CashRegister_Payment = DEFAULT_TOPIC + " /cashRegister/payment/insert/request";

}
