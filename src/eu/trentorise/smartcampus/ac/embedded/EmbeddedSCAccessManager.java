package eu.trentorise.smartcampus.ac.embedded;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import eu.trentorise.smartcampus.ac.AuthListener;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.SCAccessManager;

public class EmbeddedSCAccessManager implements SCAccessManager {

	@Override
	public void getAuthToken(Activity caller, AuthListener callback) {
		String token = caller.getSharedPreferences(Constants.ACCOUNT_TYPE,Context.MODE_PRIVATE).getString(AccountManager.KEY_AUTHTOKEN, null);
		if (token != null) {
			callback.onTokenAcquired(token);
		} else {
			Intent i = new Intent(caller, EmbeddedAuthActivity.class);
	        i.setData(Uri.parse(Constants.AUTH_REQUEST_URL));
			caller.startActivityForResult(i, 0);
		}
	}

}
