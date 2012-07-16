package eu.trentorise.smartcampus.ac;

import java.io.IOException;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;

public interface SCAccessProvider {

	String getAuthToken(Activity activity, String authority) throws OperationCanceledException, AuthenticatorException, IOException;
	String getAuthToken(Context ctx, String authority, IntentSender intentSender) throws OperationCanceledException, AuthenticatorException, IOException;
	String getAuthToken(Context ctx, String authority) throws OperationCanceledException, AuthenticatorException, IOException;

	void invalidateToken(Context ctx, String authority);
}
