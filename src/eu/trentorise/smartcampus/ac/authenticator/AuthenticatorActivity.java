package eu.trentorise.smartcampus.ac.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.provider.ContactsContract;
import eu.trentorise.smartcampus.ac.AuthActivity;
import eu.trentorise.smartcampus.ac.AuthListener;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.R;

public class AuthenticatorActivity  extends AuthActivity {
	public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

	private AccountManager mAccountManager;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      mAccountManager = AccountManager.get(this);
      super.onCreate(savedInstanceState);
    }

    @Override
	protected AuthListener getAuthListener() {
		return new AMAuthListener();
	}

	private class AMAuthListener implements AuthListener {

		@Override
		public void onTokenAcquired(String token) {
			 final Account account = new Account(getString(R.string.label), Constants.ACCOUNT_TYPE);
			 mAccountManager.addAccountExplicitly(account, null, null);
			 
	          ContentResolver.setSyncAutomatically(account,ContactsContract.AUTHORITY, true);
	          
		     Intent request = getIntent();
			 if (request.getStringExtra(Constants.KEY_AUTHORITY)!=null)
				 mAccountManager.setAuthToken(account, request.getStringExtra(Constants.KEY_AUTHORITY), token);
			 else mAccountManager.setAuthToken(account, Constants.AUTHORITY_DEFAULT, token);

			 final Intent intent = new Intent();
			 intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, Constants.ACCOUNT_NAME);
			 intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
			 intent.putExtra(AccountManager.KEY_AUTHTOKEN, token);
			 setAccountAuthenticatorResult(intent.getExtras());
			 setResult(RESULT_OK, intent);
			 
			 IntentSender sender = request.getParcelableExtra(Constants.CALLBACK_INTENT);
			 if (sender != null) {
				 try {
						Intent add = new Intent();
						add.putExtra(AccountManager.KEY_AUTHTOKEN, token);
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
			 intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, Constants.ACCOUNT_NAME);
			 intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
			 intent.putExtra(AccountManager.KEY_AUTH_FAILED_MESSAGE, error);
			 setAccountAuthenticatorResult(intent.getExtras());
			 setResult(Constants.RESULT_FAILURE, intent);
			 finish();  	    		  
		}

		@Override
		public void onAuthCancelled() {
			 final Intent intent = new Intent();
			 intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, Constants.ACCOUNT_NAME);
			 intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
			 setAccountAuthenticatorResult(intent.getExtras());
			 setResult(RESULT_CANCELED, intent);
			 finish();  	    		  
		}
		
		
    	
    }
    
}