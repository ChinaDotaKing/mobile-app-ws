package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.SpringApplicationContext;

public class SecurityConstants {
	public static final long EXPIRATION_TIME = 864000000;//10days
	public static final long PASSWORDRESET_EXPIRATION_TIME = 1000*60*60;//10days
	public static final String TOKEN_PREFIX =  "BEARER ";
	public static final String HEADER_STRING = "Authorization";
	
	public static final String SIGN_UP_URL = "/users";
	public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
	public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
	public static final String PASSWORD_RESET_URL = "/users/password-reset";
	public static final String H2_CONSOLE = "/h2-console/**";
	
	//public static final String TOKEN_SECRET = "jf9i4jgu83nfl0";

	public static String getTokenSecret()
	{
		AppProperties appProperties = (AppProperties)SpringApplicationContext.getBean("AppProperties");
		return appProperties.getTokenSecret();
	}
}
