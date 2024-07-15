package com.kamis.financemanager.controller.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.LoggingConstants;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.error.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice extends LoggingConstants {
	
	@Autowired YAMLConfig myConfig;	
	
	@ExceptionHandler(FinanceManagerException.class)
	public ResponseEntity<?> handleFinanceManagerException(FinanceManagerException e) {
		String method = "ControllerAdvice.handleGenericException";
		log.trace(ENTER_LOG, method);
		
		ErrorResponse resp = new ErrorResponse();
		resp.setMessage(e.getMessage());
		
		log.trace(EXIT_LOG, method);
		return new ResponseEntity<>(resp, e.getStatusCode());
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ErrorResponse handleUnsupportedMethodException(Exception e) {
		String method = "ControllerAdvice.handleGenericException";
		log.trace(ENTER_LOG, method);
		
		ErrorResponse resp = new ErrorResponse(myConfig.getGenericMethodNotAllowedErrorMsg());
		log.error(ERROR_TEMPLATE, method, e);
		log.trace(EXIT_LOG, method);
		return resp;
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleGenericException(Exception e) {
		String method = "ControllerAdvice.handleGenericException";
		log.trace(ENTER_LOG, method);
		
		ErrorResponse resp = new ErrorResponse(myConfig.getGenericInternalServerErrorMessage());
		log.error(ERROR_TEMPLATE, method, e);
		log.trace(EXIT_LOG, method);
		return resp;
	}
}
