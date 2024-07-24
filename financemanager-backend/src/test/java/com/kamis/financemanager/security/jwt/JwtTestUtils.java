package com.kamis.financemanager.security.jwt;

import java.util.ArrayList;
import java.util.List;

import io.restassured.http.Header;
import io.restassured.http.Headers;

public class JwtTestUtils {

	public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
	public static final String CONTENT_TYPE_HEADER_VALUE = "application/json";
	public static final String AUTH_HEADER_NAME = "Authorization";
	
	public static Headers getMockAuthHeader(String jwt) {
		Header contentType = new Header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE);
		Header authorization = new Header(AUTH_HEADER_NAME, "Bearer " + jwt);
		List<Header> headerList = new ArrayList<Header>();
		
		headerList.add(contentType);
		headerList.add(authorization);
		Headers headers = new Headers(headerList);
		return headers;
	}
}
