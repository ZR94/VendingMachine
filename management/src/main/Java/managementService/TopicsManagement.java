package managementService;

public class TopicsManagement {
	protected static final String BASE_TOPIC="/machine/+";
	
	// TOPIC PUBLISH
    protected static final String MANAGEMENT_MACHINE_STATUS_SEND=BASE_TOPIC+"/management/statusUpdate/receive";
    protected static final String MANAGEMENT_MACHINE_REVENUE_SEND_MESSAGE=BASE_TOPIC+"/management/withdrawRevenue/request";
    
    // TOPIC SUBSCRIBE

	protected static final String MANAGEMENT_MACHINE_STATUS_RECEIVE=BASE_TOPIC+"/management/statusUpdate/send";
	protected static final String MANAGEMENT_MACHINE_REVENUE_RECEIVE=BASE_TOPIC+"/management/withdrawRevenue/response";
	
}
