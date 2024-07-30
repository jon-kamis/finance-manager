package com.kamis.financemanager.controller.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.LoggingConstants;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.error.ErrorResponse;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice extends LoggingConstants {
	
	@Autowired YAMLConfig myConfig;	
	
	@ExceptionHandler(FinanceManagerException.class)
	public ResponseEntity<?> handleFinanceManagerException(FinanceManagerException e) {

		ErrorResponse resp = new ErrorResponse();
		resp.setMessage(e.getMessage());
		
		log.info("{}", e);
		return new ResponseEntity<>(resp, e.getStatusCode());
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ErrorResponse handleUnsupportedMethodException(Exception e) {

		ErrorResponse resp = new ErrorResponse(myConfig.getGenericMethodNotAllowedErrorMsg());
		
		log.info("{}", e);
		return resp;
	}
	
	@ExceptionHandler(AuthorizationDeniedException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleAuthorizationDeniedException(AuthorizationDeniedException e) {

		ErrorResponse resp = new ErrorResponse(myConfig.getGenericAccessDeniedErrorMsg());
		log.info("{}", e);
		return resp;
	}
	
	@ExceptionHandler(ExpiredJwtException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorResponse handleExpiredJwtException(ExpiredJwtException e) {
		ErrorResponse resp = new ErrorResponse(myConfig.getJwtExpiredErrorMsg());
		log.info("{}", e);
		return resp;
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleGenericException(Exception e) {

		ErrorResponse resp = new ErrorResponse(myConfig.getGenericInternalServerErrorMessage());
		log.info("{}", e);
		return resp;
	}
}
