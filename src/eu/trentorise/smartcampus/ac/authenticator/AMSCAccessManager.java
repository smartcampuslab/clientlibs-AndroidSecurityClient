package eu.trentorise.smartcampus.ac.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import eu.trentorise.smartcampus.ac.AuthListener;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.SCAccessManager;

public class AMSCAccessManager implements SCAccessManager {

	private Activity caller;
	private AuthListener callback;

	@Override
	public void getAuthToken(Activity caller, AuthListener callback) {
		this.callback = callback;
		this.caller = caller;
		AccountManager am = AccountManager.get(caller);

		am.getAuthToken(new Account(Constants.ACCOUNT_NAME,
				Constants.ACCOUNT_TYPE), Constants.AUTHTOKEN_TYPE, true,
				new OnTokenAcquired(), null);
	}

	private class OnTokenAcquired implements AccountManagerCallback<Bundle> {

		@Override
		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle = null;
			String token = null;
			try {
				bundle = result.getResult();
			} catch (Exception e) {
				Intent i = new Intent();
				i.putExtra(AccountManager.KEY_AUTH_FAILED_MESSAGE,
						e.getMessage());
				caller.setResult(Constants.RESULT_FAILURE, i);
				return;
			}
			token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
			if (token != null) {
				Intent i = new Intent();
				i.putExtra(AccountManager.KEY_AUTHTOKEN, token);
				caller.setResult(Activity.RESULT_OK);
				callback.onTokenAcquired(token);
				return;
			}
			Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
			if (launch != null) {
				caller.startActivityForResult(launch, 0);
				return;
			}
		}
	}
}
