package in.altersense.taskapp.components;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import in.altersense.taskapp.common.Config;

public class APIRequest {
	
	public static final String LOG_TAG = "APIRequest - ";

	private String url;
	private JSONObject content;
	
	private String accessToken;
	private String refreshToken;

	private Context context;
	
	private int reqCounter;
	
	private void addAccessTokenToContent() {
		try {
			this.content.put(Config.REQUEST_RESPONSE_KEYS.ACCESS_TOKEN.getKey(), accessToken);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void setAccessToken(String newAccessToken) {
		this.accessToken = newAccessToken;
		this.addAccessTokenToContent();
	}
	
	private void removeAccessTokenFromContent() {
		this.content.remove(Config.REQUEST_RESPONSE_KEYS.ACCESS_TOKEN.getKey());
	}

    public APIRequest(
            String _reqURL,
            JSONObject _content,
            Context _currentContext,
            String accessToken,
            String refreshToken) {

        this.reqCounter = 0;

        this.url = _reqURL;
        this.content = _content;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.context = _currentContext;

        this.addAccessTokenToContent();

    }

	// Constructor with context.
	public APIRequest(String _reqURL, JSONObject _content, Context _currentContext) {
        this(
                _reqURL,
                _content,
                _currentContext,
                AltEngine.readStringFromSharedPref(
                        _currentContext,
                        Config.SHARED_PREF_KEYS.ACCESS_TOKEN.getKey(),
                        ""
                ),
                AltEngine.readStringFromSharedPref(
                        _currentContext,
                        Config.SHARED_PREF_KEYS.REFRESH_TOKEN.getKey(),
                        ""
                )
        );
	}

	private String contentToString() throws Exception {
		try {
			return this.content.toString();
		} catch(NullPointerException e) {
			return "";
		} catch (Exception e) {
			throw e;
		}
	}
	
	private JSONObject httpRequest() throws Exception {
		
		Log.d(LOG_TAG,"Making HttpRequest to "+ this.url);
		JSONObject responseObject = null;
		
		HttpClient client = new DefaultHttpClient();
	    HttpParams clientParams = client.getParams();
	    HttpConnectionParams.setConnectionTimeout(clientParams, Config.CONNECTION_TIMEOUT);
	    HttpPost post;
	    HttpResponse response;
	    HttpEntity resEntity = null;
	    String responseString;
	    
	    try {
	    	
	    	post = new HttpPost(this.url);
	    	post.setHeader("Content-type", "application/json");
	    	post.setHeader("Accept", "application/json");
	    	
	    	try {
	    		post.setEntity(new StringEntity(
	    				this.contentToString(),
	    				"UTF-8"
	    				)
	    				);
	    	} catch (NullPointerException e) {
	    		Log.d(LOG_TAG, "No content so going for a blank request.");
	    	}
			Log.d(LOG_TAG, "Making Http call to "+this.url+" with content "+this.contentToString());
			response = client.execute(post);  
			resEntity = response.getEntity();
			
			if(resEntity != null) {
				responseString = EntityUtils.toString(resEntity);
				responseObject = new JSONObject(responseString);
			}
		} catch (Exception e) {
			throw e;
		}
	    return responseObject;
	}
	
	
	/**
	 * Function to generate new accessToken using refreshToken
	 */
	private void refreshAccessToken() throws Exception {
		Log.d(LOG_TAG,"    -> getNewAccessToken reached.");
		JSONObject refreshedTokenResponse = null;
		int reqCounter = 0;
        
		JSONObject content = new JSONObject();
		try {
			content.put(Config.REQUEST_RESPONSE_KEYS.REFRESH_TOKEN.getKey(), this.refreshToken);
			
			while(refreshedTokenResponse==null && reqCounter < Config.REQUEST_MAXOUT) {
				Log.d(LOG_TAG,"    -> refreshToken API call #"+reqCounter+" ("+this.refreshToken+")");
				
				JSONObject tokenRequestContent = new JSONObject();
				tokenRequestContent.put(Config.REQUEST_RESPONSE_KEYS.REFRESH_TOKEN.getKey(), this.refreshToken);
				APIRequest tokenRequestAPI = new APIRequest(
						"http://"+Config.SERVER_ADDRESS+"/user/refreshtokens",
						tokenRequestContent,
                        this.context
						);
				tokenRequestAPI.removeAccessTokenFromContent();
                refreshedTokenResponse = tokenRequestAPI.request();

				this.setAccessToken(refreshedTokenResponse.getString(Config.REQUEST_RESPONSE_KEYS.ACCESS_TOKEN.getKey()));
			}

			Log.d(LOG_TAG,"    -> refreshedTokenResponse recieved ("+this.accessToken+")");
            setUpNewTokens(refreshedTokenResponse);
			
		} catch (Exception e) {
			throw e;
		}
	}

    private void setUpNewTokens(JSONObject responseJSONObject) {
        try {
            this.refreshToken = responseJSONObject.getString(
                    Config.REQUEST_RESPONSE_KEYS.REFRESH_TOKEN.getKey()
            );
            this.accessToken = responseJSONObject.getString(
                    Config.REQUEST_RESPONSE_KEYS.ACCESS_TOKEN.getKey()
            );
            AltEngine.writeStringToSharedPref(
                    context,
                    Config.SHARED_PREF_KEYS.ACCESS_TOKEN.getKey(),
                    this.accessToken
            );
            AltEngine.writeStringToSharedPref(
                    context,
                    Config.SHARED_PREF_KEYS.REFRESH_TOKEN.getKey(),
                    this.refreshToken
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
     * Function to make simple URLReq with Content.
     * eg. API Requests are made using this function.
     */
	private JSONObject simpleURLReq() throws Exception {
		Log.d(LOG_TAG,"SimpleURLReq called.");
		JSONObject apiReqResponse = null;
		
		Log.d(LOG_TAG,"    -> APIRequest #"+this.reqCounter+" for URL "+url+" with accessToken "+accessToken);
		
		try {
			
			apiReqResponse = this.httpRequest();
			
            if(apiReqResponse.getString("status").equalsIgnoreCase(Config.RESPONSE_STATUS_FAILED)) {
                if(apiReqResponse.getInt("code")==Config.REQUEST_ERROR_CODES.ACCESS_TOKEN_EXPIRED.getCode()) {
                    Log.d(LOG_TAG,"    -> Access Token expired. refreshAccessToken() called. ("+apiReqResponse.getString("message")+")");
                    this.refreshAccessToken();

                    apiReqResponse = this.httpRequest();
                } else if(
                        apiReqResponse.getInt("code")==Config.REQUEST_ERROR_CODES.TOKEN_NOT_FOUND.getCode() ||
                        apiReqResponse.getInt("code")==Config.REQUEST_ERROR_CODES.ACCESS_TOKEN_INVALID.getCode() ||
                        apiReqResponse.getInt("code")==Config.REQUEST_ERROR_CODES.REFRESH_TOKEN_INVALID.getCode()
                        ) {
                    Log.d(LOG_TAG,"    -> Unknown error. ("+apiReqResponse.getString("message")+","+apiReqResponse.getInt("code")+")");
                    this.issueNewToken();
                    Log.d(LOG_TAG,"    -> isssueNewToken() called.");
                    apiReqResponse = this.httpRequest();
                }
            }

		} catch (Exception e) {
			throw e;
		}
		
		return apiReqResponse;
	}
	
	
	/*
	 * Function that issues new token using AppKey and AppSecrets.
	 */
	public void issueNewToken() throws Exception {
		Log.d(LOG_TAG,"AuthorizeNewToken started.");
		JSONObject authorizeAPIResponse = null;
		JSONObject authorizeAPIContent = new JSONObject();
		
		JSONObject issueTokenResponse = null;
		JSONObject issueTokenContent = new JSONObject();
		
		try {
			authorizeAPIContent.put(
                    Config.REQUEST_RESPONSE_KEYS.PASSWORD.getKey(),
                    AltEngine.readStringFromSharedPref(
                            context.getApplicationContext(),
                            Config.SHARED_PREF_KEYS.APP_SECRET.getKey(),
                            ""
                    )
            );
			authorizeAPIContent.put(
                    Config.REQUEST_RESPONSE_KEYS.EMAIL.getKey(),
                    AltEngine.readStringFromSharedPref(
                            context.getApplicationContext(),
                            Config.SHARED_PREF_KEYS.APP_KEY.getKey(),
                            ""
                    )
            );
			APIRequest authAPIRequest = new APIRequest(
					"http://"+Config.SERVER_ADDRESS+"/user/authorize",
					authorizeAPIContent,
                    this.context
					);
			
			authAPIRequest.removeAccessTokenFromContent();
//			Log.d(LOG_TAG,"    -> Authorization code request ready with content "+authorizeAPIContent.toString());
			authorizeAPIResponse = authAPIRequest.request();
			
			/*issueTokenContent.put("code", authorizeAPIResponse.getString("code"));
			APIRequest issueTokenAPIRequest = new APIRequest(
					"http://"+Config.SERVER_ADDRESS+"/users/token/issue/",
					issueTokenContent
					);
			
			authAPIRequest.removeAccessTokenFromContent();
			issueTokenResponse = issueTokenAPIRequest.request();*/
			
			this.setAccessToken(authorizeAPIResponse.getString(Config.REQUEST_RESPONSE_KEYS.ACCESS_TOKEN.getKey()));
            setUpNewTokens(authorizeAPIResponse);
			Log.d(LOG_TAG,"    -> issueToken completed. ("+this.accessToken+")");
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	/**
	 * Function that makes up the complete APIRequest class.
	 * Checks for Internet.
	 * Fires the simpleURLRequest
	 * If that fails due to expired token. Calls required function
	 * to generate the token and fires the simpleURLRequest. 
	 */
	public JSONObject request() throws Exception {
		
		JSONObject apiReqResponse = null;		
		try {
			
			do {
				apiReqResponse = this.simpleURLReq();
				this.reqCounter++;
			} while(apiReqResponse==null);
			
			// Reseting counter on successful response or request max out.
			this.reqCounter = 0;
			
		}catch (IOException e) {
			this.reqCounter++;
			if(this.reqCounter < Config.REQUEST_MAXOUT) {
				apiReqResponse = this.simpleURLReq();
			} else {
				throw new Exception(Config.REQUEST_TIMED_OUT_ERROR);
			}
		} catch (Exception e) {
			throw e;
		}
		
		return apiReqResponse;
		
	}

    public JSONObject requestWithoutTokens() throws Exception {
        this.removeAccessTokenFromContent();
        return this.request();
    }
}
