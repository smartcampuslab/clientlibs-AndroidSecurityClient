package eu.trentorise.smartcampus.ac;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SCAuthWebViewClient extends WebViewClient {
	
	private AuthListener listener = null;
	
	public SCAuthWebViewClient(AuthListener listener) {
		super();
		this.listener = listener;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (url.startsWith(Constants.AUTH_OK_URL)){
			String fragment = Uri.parse(url).getFragment();
			if (fragment != null) {
				listener.onTokenAcquired(fragment);
			} else {
				listener.onAuthFailed("No token provided");
			}
			return true;
		} if (url.startsWith(Constants.AUTH_CANCEL_URL)) {
			listener.onAuthCancelled();
			return true;
		}
	    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	    return true;
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) 
	{
		handler.proceed();
	}
}
