package com.kamis.financemanager.controller;

import com.kamis.financemanager.rest.domain.auth.RefreshTokenRequest;
import com.kamis.financemanager.util.FinanceManagerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kamis.financemanager.business.UserBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.LoggingConstants;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.auth.AuthRequest;
import com.kamis.financemanager.rest.domain.auth.JwtResponse;
import com.kamis.financemanager.rest.domain.auth.RegistrationRequest;
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

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private YAMLConfig myConfig;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private UserBusiness userBusiness;
	
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
        
		boolean authenticated = loginService.Login(request);
		
		if (authenticated) { 
            String token = jwtService.generateToken(request.getUsername());
            UUID refreshToken = jwtService.generateRefreshToken(request.getUsername());

            JwtResponse response = new JwtResponse(token, refreshToken);

			log.info("User {} has logged into the system using credentials", request.getUsername());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else { 
        	
        	log.info(LoggingConstants.WarnBadCredentialsMessage, request.getUsername());
            throw new FinanceManagerException(myConfig.getInvalidCredentialsErrorMsg(), HttpStatus.UNAUTHORIZED);
        } 	
	}

	@Operation(summary = "Refresh JWT Token using refresh token")
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
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {

		if (jwtService.validateRefreshToken(request.getRefreshToken())) {
			JwtResponse response = jwtService.generateTokensFromRefreshToken(request.getRefreshToken());

			log.info("User {} has refreshed their jwt token using a valid refresh token");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {

			log.info(LoggingConstants.WarnBadRefreshTokenMessage, request.getRefreshToken());
			throw new FinanceManagerException(myConfig.getInvalidCredentialsErrorMsg(), HttpStatus.UNAUTHORIZED);
		}
	}
	
	@Operation(summary = "Register for the application")
	@ApiResponses( value = {
			@ApiResponse(
					responseCode = "201", description = "CREATED",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessMessageResponse.class)) }),
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
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
		final String method = "AuthController.register";
		log.debug(LoggingConstants.ENTER_LOG, method);
		
        boolean success = userBusiness.registerUser(request);
        
        if (success) {
        	SuccessMessageResponse response = new SuccessMessageResponse(myConfig.getUserRegisteredMessage());
        	return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
        	throw new FinanceManagerException(myConfig.getGenericInternalServerErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
	}
}
