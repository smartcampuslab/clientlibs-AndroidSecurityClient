package eu.trentorise.smartcampus.ac.embedded;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import eu.trentorise.smartcampus.ac.Constants;

/**
 * Utility class that allows Smart Campus applications to have a common cloud
 * where sharing data.
 * 
 * @author Simone Casagranda
 * 
 */
public final class Preferences {
	
	// Keys
	private static final String TOKEN = "TOKEN";
	
	// ======================================================================= //
	// GETTERS & SETTERS
	// ======================================================================= //

	/**
	 * Retrieves token expiration in milliseconds.
	 * @throws NameNotFoundException 
	 */
	static String getAuthToken(Context context) throws NameNotFoundException{
		SharedPreferences prefs = Constants.getPrefs(context);
		return prefs.getString(TOKEN, null);
	}
	
	/**
	 * Stores a passed authentication token.
	 * @throws NameNotFoundException 
	 */
	static void setAuthToken(Context context, String token) throws NameNotFoundException{
		SharedPreferences prefs = Constants.getPrefs(context);
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
		SharedPreferences prefs = Constants.getPrefs(context);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}
	
}
