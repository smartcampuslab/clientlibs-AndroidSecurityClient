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

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
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
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }
    
	private class AMAuthListener implements AuthListener {

		@Override
		public void onTokenAcquired(String token) {
			 final Intent intent = new Intent();
		     Intent request = getIntent();
			 try {
				Preferences.setAuthToken(EmbeddedAuthActivity.this, token);
			} catch (NameNotFoundException e1) {
				Log.e(EmbeddedAuthActivity.class.getName(),""+e1.getMessage());
			}
			 //getSharedPreferences(Constants.ACCOUNT_TYPE,Context.MODE_PRIVATE).edit().putString(request.getStringExtra(Constants.KEY_AUTHORITY), token).commit();
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
