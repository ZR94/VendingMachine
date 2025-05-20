package mySparkApp.machine.support;

public class SupportTopics {
    final static String DEFAULT_TOPIC = "/machine/%d";

    //Pubish
    String SUPPORT_STATUS_SEND = DEFAULT_TOPIC + " /managment/statusUpdate/send";



    //Subscribe
    String SUPPORT_STATUS_NOTIFY = DEFAULT_TOPIC + " /managment/statusUpdate/notify";

    String SUPPORT_STATUS_RECEIVE = DEFAULT_TOPIC + " /managment/statusUpdate/receive";


    
}
