package eu.trentorise.smartcampus.ac.embedded;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * Utility class that allows Smart Campus applications to have a common cloud
 * where sharing data.
 * 
 * @author Simone Casagranda
 * 
 */
public final class Preferences {
	
	// Shared package path
	private static final String SHARED_PACKAGE = "eu.trentorise.smartcampus.launcher";
	
	// Name for preferences
	private static final String COMMON_PREF = "COMMON_PREF";
	
	// Access mode (private to application and other ones with same Shared UID)
	private static final int ACCESS = Context.MODE_PRIVATE|Context.CONTEXT_RESTRICTED;
	
	// Keys
	private static final String TOKEN = "TOKEN";
	
	private static SharedPreferences getPrefs(Context context) throws NameNotFoundException {
		Context sharedContext = context.createPackageContext(SHARED_PACKAGE, ACCESS);
		return sharedContext.getSharedPreferences(COMMON_PREF, ACCESS);
	}
	
	// ======================================================================= //
	// GETTERS & SETTERS
	// ======================================================================= //

	/**
	 * Retrieves token expiration in milliseconds.
	 * @throws NameNotFoundException 
	 */
	static String getAuthToken(Context context) throws NameNotFoundException{
		SharedPreferences prefs = getPrefs(context);
		return prefs.getString(TOKEN, null);
	}
	
	/**
	 * Stores a passed authentication token.
	 * @throws NameNotFoundException 
	 */
	static void setAuthToken(Context context, String token) throws NameNotFoundException{
		SharedPreferences prefs = getPrefs(context);
		Editor edit = prefs.edit();
		edit.putString(TOKEN, token);
		edit.commit();
	}

	// ======================================================================= //
	// OTHERS
	// ======================================================================= //
	
	/**
	 * Clears all stored preferences
	 * @throws NameNotFoundException 
	 */
	static void clear(Context context) throws NameNotFoundException{
		SharedPreferences prefs = getPrefs(context);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}
	
}
