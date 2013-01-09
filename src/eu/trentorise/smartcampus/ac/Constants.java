/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.ac;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * Constants and their read methods for security APIs
 * @author raman
 *
 */
public class Constants {

	/**
	 * Android account type used by the SmartCampus apps
	 */
    public static final String ACCOUNT_TYPE = "eu.trentorise.smartcampus.account";
    /**
     * Default token type
     */
    public static final String TOKEN_TYPE_DEFAULT = "eu.trentorise.smartcampus.account";
	/**
	 * Account type name as presented in Accounts and Synnc interface
	 */
    public static final String ACCOUNT_NAME = "SmartCampus";
    /**
     * App authority key
     */
	public static final String KEY_AUTHORITY = "eu.trentorise.smartcampus.account.AUTHORITY";
	
	/**
	 * Failure result code
	 */
	public static final int RESULT_FAILURE = 2;

	/**
	 * Android action key for the token change broadcast
	 */
	public static final String ACCOUNT_AUTHTOKEN_CHANGED_ACTION = "eu.trentorise.smartcampus.account.AUTHTOKEN_CHANGED";
	/**
	 * Android action key for the authentication 
	 */
	public static final String ACCOUNT_AUTHENTICATE_ACTION = "eu.trentorise.smartcampus.account.AUTHENTICATE";
	/**
	 * App authority default value
	 */
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
	
	private static final String DEF_AUTH_BASE_URL = "https://ac.smartcampuslab.it/ac-service-provider-web/ac/";	
	private static String baseUrl = null;


	/**
	 * Retrieve the SmartCampus authentication token request URL
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
	public static String getRequestUrl(Context context) throws NameNotFoundException {
		return getAuthUrl(context) + "getToken";
	}
	/**
	 * Retrieve the SmartCampus authentication success URL
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
	public static String getOkUrl(Context context) throws NameNotFoundException {
		return getAuthUrl(context) + "success";
	}
	/**
	 * Retrieve the SmartCampus authentication cancel URL
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
	public static String getCancelUrl(Context context) throws NameNotFoundException {
		return getAuthUrl(context) + "cancel";
	}
	/**
	 * Retrieve the SmartCampus token invalidation URL
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
	public static String getInvalidateUrl(Context context) throws NameNotFoundException {
		return getAuthUrl(context) + "invalidateToken";
	}

	/**
	 * Read the authentication base URL from the shared preferences file
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
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
	/**
	 * Write the authentication base URL to the shared preferences file.
	 * @param context
	 * @param url
	 * @throws NameNotFoundException
	 */
	public static void setAuthUrl(Context context, String url) throws NameNotFoundException {
		assert url != null;
		SharedPreferences prefs = Constants.getPrefs(context);
		Editor edit = prefs.edit();
		String newUrl = url.endsWith("/") ? url : (url+"/");
		edit.putString(P_AUTH_BASE_URL, newUrl);
		edit.commit();
		baseUrl = null;
	}
	/**
	 * Read the shared preferences file where common properties are stored
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
	public static SharedPreferences getPrefs(Context context) throws NameNotFoundException {
		Context sharedContext = context.createPackageContext(SHARED_PACKAGE, ACCESS);
		return sharedContext.getSharedPreferences(COMMON_PREF, ACCESS);
	}

}
