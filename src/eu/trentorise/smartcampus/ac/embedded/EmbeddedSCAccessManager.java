package eu.trentorise.smartcampus.ac.embedded;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import eu.trentorise.smartcampus.ac.AuthListener;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.SCAccessManager;

public class EmbeddedSCAccessManager implements SCAccessManager {

//	@Override
//	public void retrieveAuthToken(Activity caller) {
//		String token = readStoredValue(caller);
//		Intent i = new Intent(caller, EmbeddedAuthActivity.class);
//		if (token != null) {
//			i.putExtra(AccountManager.KEY_AUTHTOKEN, token);
//		} else {
//	        i.setData(Uri.parse(Constants.AUTH_REQUEST_URL));
//		}
//		caller.startActivityForResult(i, 0);
//	}

	
	
	@Override
	public String getAuthToken(final Context context, String inAuthority, final AuthListener listener) {
		final String authority = inAuthority == null ? Constants.TOKEN_TYPE_DEFAULT : inAuthority;
		String token = readStoredValue(context, authority);
		if (token != null) {
			return token;
		} else if (listener != null) {
			context.registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context ctx, Intent intent) {
					String token = readStoredValue(context, authority);
					String recTokenType = intent.getStringExtra(Constants.KEY_AUTHORITY);
					if (token != null && authority.equals(recTokenType)) {
						listener.onTokenAcquired(token);
						context.unregisterReceiver(this);
					}
				}
			}, new IntentFilter(Constants.ACCOUNT_AUTHTOKEN_CHANGED_ACTION));
			Intent i = new Intent(context, EmbeddedAuthActivity.class);
	        i.setData(Uri.parse(Constants.AUTH_REQUEST_URL));
	        i.putExtra(Constants.KEY_AUTHORITY, authority);
			context.startActivity(i);
		}
		return null;
	}

	private String readStoredValue(Context context, String tokenType) {
		return context.getSharedPreferences(Constants.ACCOUNT_TYPE,Context.MODE_PRIVATE).getString(tokenType, null);
	}

}
