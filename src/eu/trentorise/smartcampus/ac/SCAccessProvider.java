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
	
	/**
	 * Try to read the token stored, without requesting new token or signaling to the user
	 * @param ctx
	 * @param authority
	 * @return
	 */
	String readToken(Context ctx, String authority); 
	
	/**
	 * Promote the current anonymous account to the new one defined by the authority parameter.
	 * @param ctx
	 * @param authority
	 * @param authToken
	 * @return
	 */
	String promote(Activity activity, String authority, String authToken);
}
