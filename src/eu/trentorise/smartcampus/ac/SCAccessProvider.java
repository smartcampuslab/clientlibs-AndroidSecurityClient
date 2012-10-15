package eu.trentorise.smartcampus.ac;

import java.io.IOException;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;

/**
 * A reference interface for the Smart Campus Access Control library. Defines variety of method for the 
 * authentication token retrieval, being used in different scenarios, such as from the running activity
 * or from services.
 * 
 * @author raman
 *
 */
public interface SCAccessProvider {

	public static final int SC_AUTH_ACTIVITY_REQUEST_CODE = 1000;
	
	/**
	 * Retrieve the authentication token from the running activity. If the token is stored locally, it 
	 * is returned, otherwise a dedicated Authentication activity starts for result. The calling activity
	 * should implement the Activity.onActivityResult method to process the obtained token.
	 * 
	 * @param activity
	 * @param authority
	 * @return token if it is stored locally or null if the token is not present.
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws IOException
	 */
	String getAuthToken(Activity activity, String authority) throws OperationCanceledException, AuthenticatorException, IOException;
	/**
	 * Retrieve the authentication token from the an arbitrary context. If the token is stored locally, it 
	 * is returned, otherwise a notification is added to the notification bar. If the user accesses the 
	 * notification, the Authentication Activity starts and publishes the corresponding authentication
	 * result broadcast if successful (the specific authentication broadcast action depends on a specific
	 * implementation).
	 * 
	 * @param ctx
	 * @param authority
	 * @param intentSender
	 * @return token if it is stored locally or null if the token is not present.
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws IOException
	 */
	String getAuthToken(Context ctx, String authority, IntentSender intentSender) throws OperationCanceledException, AuthenticatorException, IOException;
	/**
	 * Retrieve the authentication token from the an arbitrary context. If the token is stored locally, it 
	 * is returned, otherwise the Authentication Activity starts, which, upon successful completion sends the 
	 * specified intent.
	 * @param ctx
	 * @param authority
	 * @return
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws IOException
	 */
	String getAuthToken(Context ctx, String authority) throws OperationCanceledException, AuthenticatorException, IOException;

	/** 
	 * Invalidate the authentication token locally.
	 * @param ctx
	 * @param authority
	 */
	void invalidateToken(Context ctx, String authority);
}
