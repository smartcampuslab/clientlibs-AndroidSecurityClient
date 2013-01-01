package eu.trentorise.smartcampus.ac;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;

public class Constants {

//    public static final String AUTH_BASE_URL = "https://ac.smartcampuslab.it/ac-service-provider-web/ac/";
//    public static final String AUTH_REQUEST_URL = AUTH_BASE_URL + "getToken";
//    public static final String AUTH_OK_URL = AUTH_BASE_URL + "success";
//    public static final String AUTH_CANCEL_URL = AUTH_BASE_URL + "cancel";
//    public static final String AUTH_INVALIDATE_URL = AUTH_BASE_URL + "invalidateToken";

    public static final String ACCOUNT_TYPE = "eu.trentorise.smartcampus.account";
    public static final String TOKEN_TYPE_DEFAULT = "eu.trentorise.smartcampus.account";
	public static final String ACCOUNT_NAME = "SmartCampus";
	public static final String KEY_AUTHORITY = "eu.trentorise.smartcampus.account.AUTHORITY";
	
	public static final int RESULT_FAILURE = 2;

	public static final String ACCOUNT_AUTHTOKEN_CHANGED_ACTION = "eu.trentorise.smartcampus.account.AUTHTOKEN_CHANGED";
	public static final String ACCOUNT_AUTHENTICATE_ACTION = "eu.trentorise.smartcampus.account.AUTHENTICATE";
	public static final String AUTHORITY_DEFAULT = "AUTHORITY_DEFAULT";
	public static final String CALLBACK_INTENT = "eu.trentorise.smartcampus.account.CALLBACK_INTENT";
	public static final int ACCOUNT_NOTIFICATION_ID = 1;
	public static final CharSequence ACCOUNT_NOTIFICATION_TEXT = "Problem accessing SmartCampus account.";
	public static final CharSequence ACCOUNT_NOTIFICATION_TITLE = "SmartCampus login required.";

	// Shared package path
	private static final String SHARED_PACKAGE = "eu.trentorise.smartcampus.launcher";
	
	// Name for preferences
	private static final String COMMON_PREF = "COMMON_PREF";
	
	// Access mode (private to application and other ones with same Shared UID)
	private static final int ACCESS = Context.MODE_PRIVATE|Context.CONTEXT_RESTRICTED;
	
	private static final String P_AUTH_BASE_URL= "AUTH_BASE_URL";
	
	public static String getRequestUrl(Context context) throws NameNotFoundException {
		return getAuthUrl(context) + "getToken";
	}
	public static String getOkUrl(Context context) throws NameNotFoundException {
		return getAuthUrl(context) + "success";
	}
	public static String getCancelUrl(Context context) throws NameNotFoundException {
		return getAuthUrl(context) + "cancel";
	}
	public static String getInvalidateUrl(Context context) throws NameNotFoundException {
		return getAuthUrl(context) + "invalidateToken";
	}

	private static final String DEF_AUTH_BASE_URL = "https://ac.smartcampuslab.it/ac-service-provider-web/ac/";	
	private static String baseUrl = null;

	public static String getAuthUrl(Context context) throws NameNotFoundException {
		if (baseUrl == null) {
			SharedPreferences prefs = Constants.getPrefs(context);
			baseUrl = prefs.getString(P_AUTH_BASE_URL, null);
			// tihs is temporary: the base url should always be set
			if (baseUrl == null) {
				setAuthUrl(context, DEF_AUTH_BASE_URL);
				prefs = Constants.getPrefs(context);
				baseUrl = prefs.getString(P_AUTH_BASE_URL, null);
			}
		}
		
		return baseUrl;
	}
	
	public static void setAuthUrl(Context context, String url) throws NameNotFoundException {
		assert url != null;
		SharedPreferences prefs = Constants.getPrefs(context);
		Editor edit = prefs.edit();
		String newUrl = url.endsWith("/") ? url : (url+"/");
		edit.putString(P_AUTH_BASE_URL, newUrl);
		edit.commit();
		baseUrl = null;
	}
	
	public static SharedPreferences getPrefs(Context context) throws NameNotFoundException {
		Context sharedContext = context.createPackageContext(SHARED_PACKAGE, ACCESS);
		return sharedContext.getSharedPreferences(COMMON_PREF, ACCESS);
	}

}