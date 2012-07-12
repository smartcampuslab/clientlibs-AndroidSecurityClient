package eu.trentorise.smartcampus.ac.embedded;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import eu.trentorise.smartcampus.ac.AuthActivity;
import eu.trentorise.smartcampus.ac.AuthListener;
import eu.trentorise.smartcampus.ac.Constants;

public class EmbeddedAuthActivity extends AuthActivity {
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.getStringExtra(AccountManager.KEY_AUTHTOKEN) != null) {
			 final Intent res = new Intent();
			 res.putExtra(AccountManager.KEY_AUTHTOKEN, intent.getStringExtra(AccountManager.KEY_AUTHTOKEN));
			 setResult(RESULT_OK, res);
			 finish();
        } 
		super.onCreate(savedInstanceState);
	}

    @Override
	protected AuthListener getAuthListener() {
		return new AMAuthListener();
	}

	private class AMAuthListener implements AuthListener {

		@Override
		public void onTokenAcquired(String token) {
			 final Intent intent = new Intent();
		     Intent request = getIntent();
			 getSharedPreferences(Constants.ACCOUNT_TYPE,Context.MODE_PRIVATE).edit().putString(request.getStringExtra(Constants.KEY_AUTHORITY), token).commit();
			 intent.putExtra(AccountManager.KEY_AUTHTOKEN, token);
			 setResult(RESULT_OK, intent);

			 IntentSender sender = request.getParcelableExtra(Constants.CALLBACK_INTENT);
			 if (sender != null) {
				 try {
						Intent add = new Intent();
						add.putExtra(AccountManager.KEY_AUTHTOKEN, token);
						sender.sendIntent(EmbeddedAuthActivity.this, 0, add, null, null);
					} catch (SendIntentException e) {
						e.printStackTrace();
					}
			 }
			 
			 Intent broadcast = new Intent(Constants.ACCOUNT_AUTHTOKEN_CHANGED_ACTION);
			 broadcast.putExtra(Constants.KEY_AUTHORITY, request.getStringExtra(Constants.KEY_AUTHORITY));
			 sendBroadcast(broadcast);
			 finish();  	    		  
		}

		@Override
		public void onAuthFailed(String error) {
			 final Intent intent = new Intent();
			 intent.putExtra(AccountManager.KEY_AUTH_FAILED_MESSAGE, error);
			 setResult(RESULT_CANCELED, intent);
			 finish();  	    		  
		}

		@Override
		public void onAuthCancelled() {
			 final Intent intent = new Intent();
			 setResult(RESULT_CANCELED, intent);
			 finish();  	    		  
		}
    	
    }

}
