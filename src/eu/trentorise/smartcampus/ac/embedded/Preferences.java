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
