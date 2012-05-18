package eu.trentorise.smartcampus.ac;

import android.app.Activity;

public interface SCAccessManager {

	public void getAuthToken(Activity caller, AuthListener callback);
}
