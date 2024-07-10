package com.kamis.financemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kamis.financemanager.constants.ErrorConstants;
import com.kamis.financemanager.constants.LoggingConstants;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.auth.AuthRequest;
import com.kamis.financemanager.rest.domain.auth.JwtResponse;
import com.kamis.financemanager.rest.domain.error.ErrorResponse;
import com.kamis.financemanager.rest.domain.generic.SuccessMessageResponse;
import com.kamis.financemanager.security.jwt.JwtService;
import com.kamis.financemanager.security.jwt.LoginService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private LoginService loginService;
	
	@Operation(summary = "Login to the application")
	@ApiResponses( value = {
			@ApiResponse(
					responseCode = "200", description = "OK",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class)) }),
			@ApiResponse(
					responseCode = "401", description = "UNAUTHORIZED",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(
					responseCode = "403", description = "FORBIDDEN",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(
					responseCode = "404", description = "NOT_FOUND",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(
					responseCode = "500", description = "INTERNAL_SERVER_ERROR",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
	})
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest request) {
		final String method = "AuthController.login";
		log.debug(LoggingConstants.ENTER_LOG, method);
		
        
		boolean authenticated = loginService.Login(request);
		
		if (authenticated) { 
            String token = jwtService.generateToken(request.getUsername());
            
            JwtResponse response = new JwtResponse(token);
            
    		log.debug(LoggingConstants.EXIT_LOG, method);
            return new ResponseEntity<>(response, HttpStatus.OK); 
        } else { 
        	
        	log.info(LoggingConstants.WarnBadCredentialsMessage, method, request.getUsername());
    		log.debug(LoggingConstants.EXIT_LOG, method);
            throw new FinanceManagerException(ErrorConstants.InvalidCredentialsErrorMsg, HttpStatus.UNAUTHORIZED); 
        } 	
	}
}
