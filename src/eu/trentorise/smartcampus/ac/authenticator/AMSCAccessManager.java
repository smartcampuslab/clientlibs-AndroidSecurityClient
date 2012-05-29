package eu.trentorise.smartcampus.ac.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import eu.trentorise.smartcampus.ac.AuthListener;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.SCAccessManager;

public class AMSCAccessManager implements SCAccessManager {
	
	@Override
	public String getAuthToken(Context context, String inTokenType, AuthListener listener) {
		final String tokenType = inTokenType == null ? Constants.TOKEN_TYPE_DEFAULT : inTokenType;
		AccountManager am = AccountManager.get(context);
		AccountManagerFuture<Bundle> future = am.getAuthToken(new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE), tokenType, false, new OnTokenAcquired(listener, context, tokenType), null);
		if (future.isDone()) {
			try {
				return future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

//	public void retrieveAuthToken(Activity caller) {
//		AccountManager am = AccountManager.get(caller);
//
//		am.getAuthToken(new Account(Constants.ACCOUNT_NAME,
//				Constants.ACCOUNT_TYPE), Constants.AUTHTOKEN_TYPE, true, 
//				new OnTokenAcquired(caller), null);
//	}

	private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
//		private Activity caller;
		private AuthListener listener;
		private Context context;
		private String tokenType; 
//		public OnTokenAcquired(Activity caller) {
//			this.caller = caller;
//		}
		public OnTokenAcquired(AuthListener listener, Context context, String tokenType) {
			super();
			this.listener = listener;
			this.context = context;
			this.tokenType = tokenType;
		}

		@Override
		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle = null;
			String token = null;
			try {
				bundle = result.getResult();
			} catch (Exception e) {
//				if (caller != null) {
//					Intent i = new Intent();
//					i.putExtra(AccountManager.KEY_AUTH_FAILED_MESSAGE, e.getMessage());
//					caller.setResult(Constants.RESULT_FAILURE, i);
//				} else 
				if (listener != null) {
					listener.onAuthFailed(e.getMessage());
				}
				return;
			}
			token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
			if (token != null) {
//				if (caller != null) {
//					Intent i = new Intent();
//					i.putExtra(AccountManager.KEY_AUTHTOKEN, token);
//					caller.setResult(Activity.RESULT_OK);
//				} else 
//				if (listener != null) {
//					listener.onTokenAcquired(token);
//				}
				return;
			}
			
			if (listener != null) {
				context.registerReceiver(new BroadcastReceiver() {
					@Override
					public void onReceive(Context ctx, Intent intent) {
						String token = getAuthToken(context, tokenType, null);
						String recTokenType = intent.getStringExtra(Constants.KEY_AUTHORITY);
						if (token != null && tokenType.equals(recTokenType)) {
							listener.onTokenAcquired(token);
							context.unregisterReceiver(this);
						}
					}
				}, new IntentFilter(Constants.ACCOUNT_AUTHTOKEN_CHANGED_ACTION));
			}

			Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
			if (launch != null) {
				if (context != null) {
					launch.putExtra(Constants.KEY_AUTHORITY, tokenType);
					context.startActivity(launch);
				} 
				return;
			}
		}
	}
}
