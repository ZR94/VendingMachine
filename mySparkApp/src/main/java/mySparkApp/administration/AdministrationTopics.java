package mySparkApp.administration;

public class AdministrationTopics {
    
    final static String DEFAULT_TOPIC = "/machine/%d";
	
	// TOPIC PUBLISH
    protected static final String MANAGEMENT_MACHINE_STATUS_SEND= DEFAULT_TOPIC + "/administration/statusUpdate/receive";
    protected static final String MANAGEMENT_MACHINE_REVENUE_SEND_MESSAGE= DEFAULT_TOPIC + "/administration/withdrawRevenue/request";
    
    // TOPIC SUBSCRIBE

	protected static final String MANAGEMENT_MACHINE_STATUS_RECEIVE= DEFAULT_TOPIC + "/administration/statusUpdate/send";
	protected static final String MANAGEMENT_MACHINE_REVENUE_RECEIVE= DEFAULT_TOPIC + "/administration/withdrawRevenue/response";

}
