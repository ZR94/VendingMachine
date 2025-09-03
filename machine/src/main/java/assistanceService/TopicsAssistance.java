package assistanceService;

public class TopicsAssistance {
	protected final static String BASE_TOPIC = "/machine/%d";

	// TOPIC PUBLISH
	protected static final String ASSISTANCE_SEND_EXT = BASE_TOPIC + "/management/statusUpdate/send";
	
	protected static final String ASSISTANCE_WITHDRAW_RES_EXT = BASE_TOPIC + "/management/withdrawRevenue/response";

	// TOPIC SUBSCRIBE
	// Topic su cui l'Assistance ascolta le notifiche di errore/stato
	protected static final String ASSISTANCE_STATUS_NOTIFY = BASE_TOPIC + "/assistanceService/statusUpdate/notify";
	
	protected static final String ASSISTANCE_RECEIVE_EXT = BASE_TOPIC + "/management/statusUpdate/receive";
	
	protected static final String ASSISTANCE_WITHDRAW_REQ_EXT = BASE_TOPIC + "/management/withdrawRevenue/request";
}
