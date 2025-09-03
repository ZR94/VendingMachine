package beverageService;

public class TopicsBeverage {

	protected final static String BASE_TOPIC = "/machine/%d";
	
	// TOPIC PUBLISH
    protected static final String BEVERAGE_CHOICE_BEV_RES = BASE_TOPIC + "/beverageService/chooseBeverage/response";
    
    protected static final String BEVERAGE_VERIFY_CREDIT_REQ = BASE_TOPIC + "/cashboxService/verifyCredit/request";
    
    protected static final String BEVERAGE_STATUS_NOTIFY = BASE_TOPIC + "/assistanceService/statusUpdate/notify";
    
    // TOPIC SUBSCRIBE
    protected static final String BEVERAGE_CHOICE_BEV_REQ = BASE_TOPIC + "/beverageService/chooseBeverage/request";
    
    protected static final String BEVERAGE_VERIFY_CREDIT_RES = BASE_TOPIC + "/beverageService/verifyCredit/response";
}
