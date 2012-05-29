package eu.trentorise.smartcampus.ac;

import android.content.Context;

public interface SCAccessManager {

	public String getAuthToken(Context context, String authority, AuthListener listener);
}
