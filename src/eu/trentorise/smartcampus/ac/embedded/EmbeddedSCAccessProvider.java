package eu.trentorise.smartcampus.ac.embedded;

import java.io.IOException;

import android.R;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.SCAccessProvider;

public class EmbeddedSCAccessProvider implements SCAccessProvider{

	@Override
	public String getAuthToken(Activity activity, String inAuthority) throws OperationCanceledException, AuthenticatorException, IOException {
		final String authority = inAuthority == null ? Constants.AUTHORITY_DEFAULT : inAuthority;
		String token = readStoredValue(activity, authority);
		if (token != null) {
			return token;
		}
		Intent i = new Intent(activity, EmbeddedAuthActivity.class);
        try {
			i.setData(Uri.parse(Constants.getRequestUrl(activity)));
		} catch (NameNotFoundException e) {
			throw new AuthenticatorException("No auth url provided");
		}
        i.putExtra(Constants.KEY_AUTHORITY, authority);
		activity.startActivityForResult(i, SCAccessProvider.SC_AUTH_ACTIVITY_REQUEST_CODE);
		return null;
	}

	@Override
	public String getAuthToken(Context ctx, String inAuthority, IntentSender intentSender) throws OperationCanceledException, AuthenticatorException, IOException {
		final String authority = inAuthority == null ? Constants.AUTHORITY_DEFAULT : inAuthority;
		String token = readStoredValue(ctx, authority);
		if (token != null) {
			return token;
		}
		Intent i = new Intent(ctx, EmbeddedAuthActivity.class);
        try {
			i.setData(Uri.parse(Constants.getRequestUrl(ctx)));
		} catch (NameNotFoundException e) {
			throw new AuthenticatorException("No auth url provided");
		}
        i.putExtra(Constants.KEY_AUTHORITY, authority);
		i.putExtra(Constants.CALLBACK_INTENT, intentSender);
		ctx.startActivity(i);
		return null;
	}

	@Override
	public String getAuthToken(Context ctx, String inAuthority) throws OperationCanceledException, AuthenticatorException, IOException {
		final String authority = inAuthority == null ? Constants.AUTHORITY_DEFAULT : inAuthority;
		String token = readStoredValue(ctx, authority);
		if (token != null) {
			return token;
		}
		Intent i = new Intent(ctx, EmbeddedAuthActivity.class);
        try {
			i.setData(Uri.parse(Constants.getRequestUrl(ctx)));
		} catch (NameNotFoundException e) {
			throw new AuthenticatorException("No auth url provided");
		}
        i.putExtra(Constants.KEY_AUTHORITY, authority);

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(ns);
        
        int icon = R.drawable.stat_notify_error;
        CharSequence tickerText = Constants.ACCOUNT_NOTIFICATION_TITLE;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        CharSequence contentText = Constants.ACCOUNT_NOTIFICATION_TEXT;
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, SC_AUTH_ACTIVITY_REQUEST_CODE, i, 0);
        notification.setLatestEventInfo(ctx, contentText, null, contentIntent);
        
        mNotificationManager.notify(Constants.ACCOUNT_NOTIFICATION_ID, notification);
        
        return null;
	}

	private String readStoredValue(Context context, String authority) {
		try {
			return Preferences.getAuthToken(context);
		} catch (NameNotFoundException e) {
			return null;
		}//context.getSharedPreferences(Constants.ACCOUNT_TYPE,Context.MODE_PRIVATE).getString(authority, null);
	}

	@Override
	public void invalidateToken(Context context, String inAuthority) {
		try {
			Preferences.clear(context);
		} catch (NameNotFoundException e) {
			Log.e(getClass().getName(), ""+e.getMessage());
		}
//		final String authority = inAuthority == null ? Constants.AUTHORITY_DEFAULT : inAuthority;
//		context.getSharedPreferences(Constants.ACCOUNT_TYPE,Context.MODE_PRIVATE).edit().putString(authority, null).commit();		
	}

	@Override
	public String readToken(Context ctx, String inAuthority) {
		final String authority = inAuthority == null ? Constants.AUTHORITY_DEFAULT : inAuthority;
		return readStoredValue(ctx, authority);
	}

	
}
