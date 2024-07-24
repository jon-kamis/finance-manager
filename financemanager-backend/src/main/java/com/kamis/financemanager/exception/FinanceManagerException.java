package com.kamis.financemanager.exception;

import org.springframework.http.HttpStatusCode;

public class FinanceManagerException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HttpStatusCode statusCode;
	private String message;

	public FinanceManagerException() {}
	
	public FinanceManagerException(String message, HttpStatusCode statusCode) {
		this.statusCode = statusCode;
		this.message = message;
		
	}
	
	public FinanceManagerException(String message) {
		this.message = message;
		
	}
	
	public HttpStatusCode getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(HttpStatusCode statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
