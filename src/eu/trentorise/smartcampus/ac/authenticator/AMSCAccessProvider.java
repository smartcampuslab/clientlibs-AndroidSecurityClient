package eu.trentorise.smartcampus.ac.authenticator;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.SCAccessProvider;

public class AMSCAccessProvider implements SCAccessProvider {
	
	
	@Override
	public String getAuthToken(Context ctx, String inAuthority) throws OperationCanceledException, AuthenticatorException,
			IOException {
		final String authority = inAuthority == null ? Constants.AUTHORITY_DEFAULT : inAuthority;
		AccountManager am = AccountManager.get(ctx);
		AccountManagerFuture<Bundle> future = am.getAuthToken(new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE), authority, true, null, null); 
		String token = null;
		if (future.isDone()) {
			token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
		}
		return token;
	}

	@Override
	public String getAuthToken(final Activity activity, String inAuthority) throws OperationCanceledException, AuthenticatorException, IOException {
		final String authority = inAuthority == null ? Constants.AUTHORITY_DEFAULT : inAuthority;
		AccountManager am = AccountManager.get(activity);
		AccountManagerFuture<Bundle> future = am.getAuthToken(new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE), authority, false, 
				new AccountManagerCallback<Bundle>() {

					@Override
					public void run(AccountManagerFuture<Bundle> result) {
						Bundle bundle = null;
						try {
							bundle = result.getResult();
							Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
							if (launch != null) {
									launch.putExtra(Constants.KEY_AUTHORITY, authority);
									activity.startActivityForResult(launch, 0);
							}
						} catch (Exception e) {
							return;
						}
					}
				}
		, null);
		String token = null;
		if (future.isDone()) {
			token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
		}
		return token;
	}

	@Override
	public String getAuthToken(Context ctx, String inAuthority, IntentSender intentSender) throws OperationCanceledException, AuthenticatorException, IOException {
		final String authority = inAuthority == null ? Constants.AUTHORITY_DEFAULT : inAuthority;
		AccountManager am = AccountManager.get(ctx);
		AccountManagerFuture<Bundle> future = am.getAuthToken(new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE), authority, false, new OnTokenAcquired(ctx, authority, intentSender), null);
		String token = null;
		if (future.isDone()) {
			token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
		}
		return token;
	}

	private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
		private Context context;
		private String authority; 
		private IntentSender intentSender;

		public OnTokenAcquired(Context context, String authority,IntentSender intentSender) {
			super();
			this.context = context;
			this.authority = authority;
			this.intentSender = intentSender;
		}

		@Override
		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle = null;
			String token = null;
			try {
				bundle = result.getResult();
			} catch (Exception e) {
				return;
			}
			token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
			if (token != null) {
				if (intentSender != null) {
					try {
						Intent add = new Intent();
						add.putExtra(AccountManager.KEY_AUTHTOKEN, token);
						intentSender.sendIntent(context, 0, add, null, null);
					} catch (SendIntentException e) {
						return;
					}
				}
				return;
			}

			Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
			if (launch != null) {
				if (context != null) {
					launch.putExtra(Constants.KEY_AUTHORITY, authority);
					launch.putExtra(Constants.CALLBACK_INTENT, intentSender);
					context.startActivity(launch);
				} 
				return;
			}
		}
	}
}
