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

package eu.trentorise.smartcampus.ac.network;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;
import eu.trentorise.smartcampus.ac.model.UserData;

/**
 * @author raman
 *
 */
public class RemoteConnector {

    /** address of the create anonymous endpoint */
	private static final String PATH_CREATE_ANONYMOUS = "/getToken/anonymous";
    /** address of the code validation endpoint */
	private static final String PATH_VALIDATE = "/validateCode";
	/** Timeout (in ms) we specify for each http request */
    public static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000;
    /** The tag used to log to adb console. */
    private static final String TAG = "RemoteConnector";

	public static UserData validateAccessCode(String service, String code) {

        final HttpResponse resp;
        final HttpEntity entity = null;
        Log.i(TAG, "validating code: " + code);
        final HttpPost post = new HttpPost(service + PATH_VALIDATE+"/"+code);
        post.setEntity(entity);
        post.setHeader("Accept", "application/json");
        try {
            resp = getHttpClient().execute(post);
            final String response = EntityUtils.toString(resp.getEntity());
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	JSONObject json = new JSONObject(response);
            	UserData data = UserData.valueOf(json);
                Log.v(TAG, "Successful authentication");
                return data;
            }
            Log.e(TAG, "Error validating " + resp.getStatusLine());
            return null;
        } catch (final Exception e) {
            Log.e(TAG, "IOException when getting authtoken", e);
            return null;
        } finally {
            Log.v(TAG, "getAuthtoken completing");
        }
	}  
	
	public static UserData createAnonymousUser(String service, String externalId) {
        String requestString = service + PATH_CREATE_ANONYMOUS;
        Log.i(TAG, "create anonymous user: " + externalId);
        if (externalId != null) {
        	requestString += "?externalId="+externalId;
        } else {
            Log.i(TAG, "Failed creating anonymous: external ID requred");
            return null;
        }
		final HttpGet request = new HttpGet(requestString);
		// HTTP parameters stores header etc.
		final HttpParams params = new BasicHttpParams();
		HttpClientParams.setRedirecting(params, false);
        request.setParams(params);
        request.setHeader("Accept", "application/json");
		String accessCode = null;
        try {
			final HttpResponse resp = getHttpClient().execute(request);
			Header locationHeader = resp.getFirstHeader("location");
			if (locationHeader != null && locationHeader.getValue().indexOf('#') > 0) {
				accessCode = locationHeader.getValue().substring(locationHeader.getValue().indexOf('#')+1);
			}
			if ((accessCode != null) && (accessCode.length() > 0)) {
			    Log.v(TAG, "Successfully created: "+externalId);
			    return validateAccessCode(service, accessCode);
			} else {
			    Log.e(TAG, "Error creating anonymous: http code " + resp.getStatusLine());
			    return null;
			}
		} catch (Exception e) {
		    Log.e(TAG, "Error creating anonymous: " + e.getMessage());
			return null;
		}
	}
	
    private static HttpClient getHttpClient() {
        HttpClient httpClient = new DefaultHttpClient();
        final HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        ConnManagerParams.setTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        return httpClient;
    }

}
