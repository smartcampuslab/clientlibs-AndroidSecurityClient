package eu.trentorise.smartcampus.ac.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import eu.trentorise.smartcampus.ac.AuthListener;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.R;
import eu.trentorise.smartcampus.ac.SCAuthWebViewClient;

public class AuthenticatorActivity  extends AccountAuthenticatorActivity {
	public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    private WebView mWebView;
	private AccountManager mAccountManager;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_PROGRESS);
      mAccountManager = AccountManager.get(this);
      setUpWebView();
    }
    
    private void setUpWebView() {
    	setContentView(R.layout.web);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVisibility(View.VISIBLE);
        
        mWebView.setWebViewClient(new SCAuthWebViewClient(new AMAuthListener()));

        Intent intent = getIntent();
        if (intent.getData() != null) {
          mWebView.loadUrl(intent.getDataString());
        }
    }

    private class AMAuthListener implements AuthListener {

		@Override
		public void onTokenAcquired(String token) {
			 final Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
			 mAccountManager.addAccountExplicitly(account, null, null);
			 mAccountManager.setAuthToken(account, Constants.AUTHTOKEN_TYPE, token);
			 final Intent intent = new Intent();
			 intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, Constants.ACCOUNT_NAME);
			 intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
			 intent.putExtra(AccountManager.KEY_AUTHTOKEN, token);
			 setAccountAuthenticatorResult(intent.getExtras());
			 setResult(RESULT_OK, intent);
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