package eu.trentorise.smartcampus.ac;

public class Constants {

    public static final String AUTH_BASE_URL = "https://192.168.255.75:8443/smartcampus.security.web/";
    public static final String AUTH_REQUEST_URL = AUTH_BASE_URL + "auth";
    public static final String AUTH_OK_URL = AUTH_BASE_URL + "result";
    public static final String AUTH_CANCEL_URL = AUTH_BASE_URL + "cancel";

    public static final String ACCOUNT_TYPE = "eu.trentorise.smartcampus.account";
    public static final String AUTHTOKEN_TYPE = "eu.trentorise.smartcampus.account";
	public static final String ACCOUNT_NAME = "eu.trentorise.smartcampus.account";
	
//	public static final String KEY_AUTHTOKEN = "eu.trentorise.smartcampus.token";
//	public static final String KEY_AUTHERROR = "eu.trentorise.smartcampus.error";
	public static final int RESULT_FAILURE = 2;
}