package displayService;

public class TopicsDisplay {
	
	protected final static String BASE_TOPIC = "/machine/%d";
	
	// TOPIC PUBLISH
    protected static final String DISPLAY_INSERT_COIN_REQ = BASE_TOPIC+"/cashboxService/insert/request";
    protected static final String DISPLAY_CURR_CREDIT_REQ = BASE_TOPIC+"/cashboxService/credit/request";
    protected static final String DISPLAY_RETURN_CREDIT_REQ = BASE_TOPIC + "/cashboxService/returnCredit/request";
    
    protected static final String DISPLAY_CHOICE_BEV_REQ = BASE_TOPIC + "/beverageService/chooseBeverage/request";
    
    // TOPIC SUBSCRIBE
    protected static final String DISPLAY_CURR_CREDIT_RES = BASE_TOPIC+"/displayService/credit/response";
    protected static final String DISPLAY_RETURN_CREDIT_RES = BASE_TOPIC + "/displayService/returnCredit/response";
    
    protected static final String DISPLAY_CHOICE_BEV_RES = BASE_TOPIC + "/beverageService/chooseBeverage/response";
    
}
