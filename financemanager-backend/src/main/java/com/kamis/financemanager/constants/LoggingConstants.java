package com.kamis.financemanager.constants;

public class LoggingConstants {

	//Enter Exit Logs
	public static final String ENTER_LOG = "ENTER {}";
	public static final String EXIT_LOG = "EXIT {}";
	
	//Messages
	public static final String WarnBadCredentialsMessage = "User {} attempted to log into the system with incorrect credentials";
	public static final String WarnBadRefreshTokenMessage = "User attempted to refresh JWT token using invalid refresh token: {}";

}
