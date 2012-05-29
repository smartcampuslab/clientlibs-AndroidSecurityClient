package eu.trentorise.smartcampus.ac.embedded;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import eu.trentorise.smartcampus.ac.AuthListener;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.R;
import eu.trentorise.smartcampus.ac.SCAuthWebViewClient;

public class EmbeddedAuthActivity extends Activity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_PROGRESS);
      setUpWebView();
    }
    
    private void setUpWebView() {
    	setContentView(R.layout.web);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        if (intent.getStringExtra(AccountManager.KEY_AUTHTOKEN) != null) {
			 final Intent res = new Intent();
			 res.putExtra(AccountManager.KEY_AUTHTOKEN, intent.getStringExtra(AccountManager.KEY_AUTHTOKEN));
			 setResult(RESULT_OK, res);
			 finish();
        } else {
            mWebView.setWebViewClient(new SCAuthWebViewClient(new AMAuthListener()));
            if (intent.getData() != null) {
            	String url = intent.getDataString();
            	if (intent.getStringExtra(Constants.KEY_AUTHORITY) != null) {
            		url += (url.endsWith("/")?intent.getStringExtra(Constants.KEY_AUTHORITY):"/"+intent.getStringExtra(Constants.KEY_AUTHORITY));
            	}
              mWebView.loadUrl(url);
            }
        }
    }

    private class AMAuthListener implements AuthListener {

		@Override
		public void onTokenAcquired(String token) {
			 final Intent intent = new Intent();
			 getSharedPreferences(Constants.ACCOUNT_TYPE,Context.MODE_PRIVATE).edit().putString(getIntent().getStringExtra(Constants.KEY_AUTHORITY), token).commit();
			 intent.putExtra(AccountManager.KEY_AUTHTOKEN, token);
			 setResult(RESULT_OK, intent);
			 
			 Intent broadcast = new Intent(Constants.ACCOUNT_AUTHTOKEN_CHANGED_ACTION);
			 broadcast.putExtra(Constants.KEY_AUTHORITY, getIntent().getStringExtra(Constants.KEY_AUTHORITY));
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
