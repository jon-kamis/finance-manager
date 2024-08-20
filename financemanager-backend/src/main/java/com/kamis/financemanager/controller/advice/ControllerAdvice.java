package com.kamis.financemanager.controller.advice;

import com.kamis.financemanager.util.FinanceManagerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice extends LoggingConstants {
	
	@Autowired YAMLConfig myConfig;	

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleMissingServletRequestParameterException(Exception e) {
		log.info("User {} hit bad request with message {}", FinanceManagerUtil.getLoggedInUserName(), e.getMessage());
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(FinanceManagerException.class)
	public ResponseEntity<?> handleFinanceManagerException(FinanceManagerException e) {
		log.info(e.getMessage());
		return new ResponseEntity<>(new ErrorResponse(e.getMessage()), e.getStatusCode());
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ErrorResponse handleUnsupportedMethodException(Exception e) {
		log.info(e.getMessage());
        return new ErrorResponse(myConfig.getGenericMethodNotAllowedErrorMsg());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNoResourceFoundException(Exception e) {
		log.info(e.getMessage());
		return new ErrorResponse(myConfig.getGenericNotFoundMessage());
	}
	
	@ExceptionHandler(AuthorizationDeniedException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleAuthorizationDeniedException(AuthorizationDeniedException e) {
		log.info(e.getMessage());
        return new ErrorResponse(myConfig.getGenericAccessDeniedErrorMsg());
	}
	
	@ExceptionHandler(ExpiredJwtException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorResponse handleExpiredJwtException(ExpiredJwtException e) {
		log.info(e.getMessage());
        return new ErrorResponse(myConfig.getJwtExpiredErrorMsg());
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleGenericException(Exception e) {
		log.info(e.getMessage());
		log.error("Stack Trace: ", e);
        return new ErrorResponse(myConfig.getGenericInternalServerErrorMessage());
	}
}
