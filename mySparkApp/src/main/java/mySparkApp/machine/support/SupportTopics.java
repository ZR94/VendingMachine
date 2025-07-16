package mySparkApp.machine.support;

public class SupportTopics {
    final static String DEFAULT_TOPIC = "/machine/%d";

    //Pubish
    protected static final String SUPPORT_STATUS_SEND = DEFAULT_TOPIC + " /managment/statusUpdate/send";

    protected static final String SUPPORT_STATUS_WITHDRAW_RES = DEFAULT_TOPIC + "/management/withdrawRevenue/response";

    //Subscribe
    protected static final String SUPPORT_STATUS_NOTIFY = DEFAULT_TOPIC + " /managment/statusUpdate/notify";

    protected static final String SUPPORT_STATUS_RECEIVE = DEFAULT_TOPIC + " /managment/statusUpdate/receive";

    protected static final String SUPPORT_STATUS_WITHDRAW_REQ = DEFAULT_TOPIC + "/management/withdrawRevenue/request";
    
}
