package cashboxService;

public class TopicsCashbox {

	protected final static String BASE_TOPIC = "/machine/%d";

	// TOPIC PUBLISH
	protected static final String CASHBOX_CURR_CREDIT_RES = BASE_TOPIC + "/displayService/credit/response";
	protected static final String CASHBOX_RETURN_CREDIT_RES = BASE_TOPIC + "/displayService/returnCredit/response";

	protected static final String CASHBOX_VERIFY_CREDIT_RES = BASE_TOPIC + "/beverageService/verifyCredit/response";
	
    protected static final String CASHBOX_STATUS_NOTIFY = BASE_TOPIC + "/assistanceService/statusUpdate/notify";

	// TOPIC SUBSCRIBE
	protected static final String CASHBOX_INSERT_COIN_REQ = BASE_TOPIC + "/cashboxService/insert/request";
	protected static final String CASHBOX_CURR_CREDIT_REQ = BASE_TOPIC + "/cashboxService/credit/request";
	protected static final String CASHBOX_RETURN_CREDIT_REQ = BASE_TOPIC + "/cashboxService/returnCredit/request";

	protected static final String CASHBOX_VERIFY_CREDIT_REQ = BASE_TOPIC + "/cashboxService/verifyCredit/request";

}
