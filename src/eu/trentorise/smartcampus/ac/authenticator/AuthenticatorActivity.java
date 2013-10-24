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
package eu.trentorise.smartcampus.ac.authenticator;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import eu.trentorise.smartcampus.ac.AuthActivity;
import eu.trentorise.smartcampus.ac.AuthListener;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.DeviceUuidFactory;
import eu.trentorise.smartcampus.ac.R;
import eu.trentorise.smartcampus.ac.model.UserData;
import eu.trentorise.smartcampus.ac.network.RemoteConnector;

/**
 *  Implementation of the {@link AuthActivity} storing the acquired token
 * in the {@link AccountManager} infrastructure and broadcasting the result event.
 * @author raman
 *
 */
public class AuthenticatorActivity  extends AuthActivity {
	public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
	private static final int RC_ACCOUNT_PICK = 200;
	private final static String USERINFO_SCOPE =    "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
	protected static final int RC_AUTH = 201;
	
	private String mAccountName = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
    }

	@Override
	protected void setUp() {
	     Intent request = getIntent();
	     String authTokenType = request.getStringExtra(Constants.KEY_AUTHORITY)!=null ? 
	    		 request.getStringExtra(Constants.KEY_AUTHORITY) : Constants.AUTHORITY_DEFAULT;

	     if (Constants.TOKEN_TYPE_ANONYMOUS.equals(authTokenType)) {
    		new AnonymAccountAsyncTask().execute();
    	} else { 
    		int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    		if (code != ConnectionResult.SUCCESS) {
		         Dialog alert = GooglePlayServicesUtil.getErrorDialog(
			             code,
			             AuthenticatorActivity.this,
			             RC_AUTH,
			             new OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								getAuthListener().onAuthCancelled();
							}
						});
		         alert.show();
    			return;
    		}
    		
			AccountManager mAccountManager = AccountManager.get(getApplicationContext());
			Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
			if (mAccountName == null && (accounts == null || accounts.length != 1)) {
				Intent intent = AccountPicker.newChooseAccountIntent(
						null, 
						null, 
						new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, 
						false, null, 
						null, 
						null, 
						null);
				startActivityForResult(intent, RC_ACCOUNT_PICK);
			} else {
				new ExtAccountAsyncTask().execute(mAccountName != null ? mAccountName : accounts[0].name);
			}
//    		super.setUp();
    	}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RC_ACCOUNT_PICK && resultCode == RESULT_OK) {
			mAccountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			new ExtAccountAsyncTask().execute(mAccountName);
		} else if (requestCode == RC_AUTH  && resultCode == RESULT_OK) {
			setUp();
	   } else {
		   getAuthListener().onAuthFailed("user failure");
	   }	
	}


	@Override
	protected AuthListener getAuthListener() {
		return new AMAuthListener();
	}

	/**
	 * @author raman
	 *
	 */
	private class AnonymAccountAsyncTask extends
			AsyncTask<Void, Void, UserData> {
		private ProgressDialog progress = null;

		@Override
		protected UserData doInBackground(Void... params) {
			try {
				return RemoteConnector.createAnonymousUser(Constants.getAuthUrl(AuthenticatorActivity.this), new DeviceUuidFactory(AuthenticatorActivity.this).getDeviceUuid().toString());
			} catch (NameNotFoundException e) {
				Log.e(Authenticator.class.getName(), "Failed to create anonymous user: "+ e.getMessage());
				return null;
			}
		}

		protected void onPostExecute(UserData result) {
			if (progress != null) {
				try {
					progress.cancel();
				} catch (Exception e) {
					Log.w(getClass().getName(),"Problem closing progress dialog: "+e.getMessage());
				}
			}
			if (result != null && result.getToken() != null) {
				getAuthListener().onTokenAcquired(result);
			} else {
				getAuthListener().onAuthFailed("Failed to create account");
			}
		}

		@Override
		protected void onPreExecute() {
			progress  = ProgressDialog.show(AuthenticatorActivity.this, "", AuthenticatorActivity.this.getString(R.string.auth_in_progress), true);
			super.onPreExecute();
		}

	}

	private class ExtAccountAsyncTask extends
			AsyncTask<String, Void, UserData> {
		private ProgressDialog progress = null;
		private Exception e = null;

		@Override
		protected UserData doInBackground(String... params) {
			try {
				String token = GoogleAuthUtil.getToken(AuthenticatorActivity.this, params[0], "oauth2:" + USERINFO_SCOPE);
				return RemoteConnector.createUserWithToken(Constants.getAuthUrl(AuthenticatorActivity.this), "googleext", token);
			} catch (Exception e) {
				Log.e(Authenticator.class.getName(), "Failed to create user: "+ e.getMessage());
		        this.e = e;
				return null;
			}
		}

		protected void onPostExecute(UserData result) {
			if (progress != null) {
				try {
					progress.cancel();
				} catch (Exception e) {
					Log.w(getClass().getName(),
							"Problem closing progress dialog: "
									+ e.getMessage());
				}
			}
			if (result != null && result.getToken() != null) {
				getAuthListener().onTokenAcquired(result);
			} else if (e != null){
				if (e instanceof GooglePlayServicesAvailabilityException) {
			         Dialog alert = GooglePlayServicesUtil.getErrorDialog(
				             ((GooglePlayServicesAvailabilityException)e).getConnectionStatusCode(),
				             AuthenticatorActivity.this,
				             RC_AUTH);
			         alert.show();
				} else if (e instanceof UserRecoverableAuthException) {
			          AuthenticatorActivity.this.startActivityForResult(
			                  ((UserRecoverableAuthException)e).getIntent(),
			                  RC_AUTH);
				}
			} else {
				getAuthListener().onAuthFailed("Failed to create account");
			}
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(AuthenticatorActivity.this, "",
					AuthenticatorActivity.this
							.getString(R.string.auth_in_progress), true);
			super.onPreExecute();
		}

	}

	private class AMAuthListener implements AuthListener {

		@Override
		public void onTokenAcquired(UserData data) {
			 final Account account = new Account(Constants.getAccountName(AuthenticatorActivity.this), Constants.getAccountType(AuthenticatorActivity.this));
			 AccountManager mAccountManager = AccountManager.get(getApplicationContext());
			 Bundle dataBundle = new Bundle();
			 try {
				dataBundle.putString(AccountManager.KEY_USERDATA, data.toJSON().toString());
			} catch (JSONException e1) {
				Log.e(AuthenticatorActivity.class.getName(), "Failed to write UserData: "+e1.getMessage());
			}
//			 Account[] accounts = mAccountManager.getAccountsByType(account.type);
//			 if (accounts != null) {
//				for (int i = 0; i < accounts.length; i++) {
//					mAccountManager.removeAccount(accounts[i], null, null);
//				}
//			 }
			 mAccountManager.addAccountExplicitly(account, null, dataBundle);
			 
//			 accounts = mAccountManager.getAccountsByType(account.type);
			 
	         ContentResolver.setSyncAutomatically(account,ContactsContract.AUTHORITY, true);
	          
		     Intent request = getIntent();
		     final String authority = request.getStringExtra(Constants.KEY_AUTHORITY)!=null && 
		    		 !request.getStringExtra(Constants.KEY_AUTHORITY).equals(Constants.TOKEN_TYPE_ANONYMOUS) ? 
		    		 request.getStringExtra(Constants.KEY_AUTHORITY) : Constants.AUTHORITY_DEFAULT;
				 
		     mAccountManager.setAuthToken(account, authority, data.getToken());
		     if (request.getStringExtra(Constants.KEY_AUTHORITY).equals(Constants.TOKEN_TYPE_ANONYMOUS)) {
		    	 mAccountManager.setAuthToken(account, Constants.TOKEN_TYPE_ANONYMOUS, data.getToken());
				 mAccountManager.setUserData(account, Constants.KEY_AUTHORITY, Constants.TOKEN_TYPE_ANONYMOUS);
		     } else {
				 mAccountManager.setUserData(account, Constants.KEY_AUTHORITY, authority);
		     }

			 final Intent intent = new Intent();
			 intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
			 intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			 intent.putExtra(AccountManager.KEY_AUTHTOKEN, data.getToken());

			 // this is workaround that is needed on some devices: without it the 
			 // getuserdata may return null. the problem is with the bug in the in-memory account data caching
			 mAccountManager.setUserData(account, AccountManager.KEY_USERDATA, dataBundle.getString(AccountManager.KEY_USERDATA));
			 
			 setAccountAuthenticatorResult(intent.getExtras());
			 setResult(RESULT_OK, intent);
			 
			 IntentSender sender = request.getParcelableExtra(Constants.CALLBACK_INTENT);
			 if (sender != null) {
				 try {
						Intent add = new Intent();
						add.putExtra(AccountManager.KEY_AUTHTOKEN, data.getToken());
						add.putExtra(AccountManager.KEY_USERDATA, data);
						sender.sendIntent(AuthenticatorActivity.this, 0, add, null, null);
					} catch (SendIntentException e) {
						e.printStackTrace();
					}
			 }
			 
			 finish();  	    		  
		}

		@Override
		public void onAuthFailed(String error) {
			 final Intent intent = new Intent();
			 intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, Constants.getAccountName(AuthenticatorActivity.this));
			 intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.getAccountType(AuthenticatorActivity.this));
			 intent.putExtra(AccountManager.KEY_AUTH_FAILED_MESSAGE, error);
			 setAccountAuthenticatorResult(intent.getExtras());
			 setResult(Constants.RESULT_FAILURE, intent);
			 finish();  	    		  
		}

		@Override
		public void onAuthCancelled() {
			 final Intent intent = new Intent();
			 intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, Constants.getAccountName(AuthenticatorActivity.this));
			 intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.getAccountType(AuthenticatorActivity.this));
			 if (getIntent() != null && getIntent().hasExtra(Constants.PROMOTION_TOKEN)) {
				 String uData = getIntent().getStringExtra(Constants.OLD_DATA);
				 if (uData != null) {
					 UserData data;
					try {
						data = UserData.valueOf(new JSONObject(uData));
						getIntent().putExtra(Constants.KEY_AUTHORITY, Constants.TOKEN_TYPE_ANONYMOUS);
						onTokenAcquired(data);
						return;
					} catch (JSONException e) {
						Log.e(AuthenticatorActivity.class.getName(), "Failed to revert to old token: "+e.getMessage());
					}
				 } 
			 }
			 setAccountAuthenticatorResult(intent.getExtras());
			 setResult(RESULT_CANCELED, intent);
			 finish();  	    		  
		}
		
		
    	
    }
    
}
