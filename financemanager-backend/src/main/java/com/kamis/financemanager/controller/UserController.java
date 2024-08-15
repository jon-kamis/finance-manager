package com.kamis.financemanager.controller;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.users.UserMonthlySummaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kamis.financemanager.business.UserBusiness;
import com.kamis.financemanager.rest.domain.error.ErrorResponse;
import com.kamis.financemanager.rest.domain.users.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
	
	@Autowired
	UserBusiness userBusiness;

	@Autowired
	YAMLConfig myConfig;
	
	@Operation(summary = "Get a user by its id")
	@ApiResponses( value = {
			@ApiResponse(
					responseCode = "200", description = "OK",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)) }),
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
	@GetMapping("/{id}")
	public UserResponse getUserById(@Parameter(description = "id of the user to retrieve") @PathVariable int id) {
		log.info("searching for user with id {}", id);
		return userBusiness.getUserById(id);
	}

	@Operation(summary = "Get a user by its id")
	@ApiResponses( value = {
			@ApiResponse(
					responseCode = "200", description = "OK",
					content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserMonthlySummaryResponse.class)) }),
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
	@PreAuthorize("@securityService.hasAccess(authentication, #id)")
	@GetMapping("/{id}/summary/{yearMonth}")
	public ResponseEntity<?> getUserById(@Parameter(description = "id of the user to retrieve") @PathVariable int id,
										 @Parameter(description = "year and month to retrieve", required = true, schema = @Schema(example ="yyyy-mm")) @PathVariable String yearMonth) {

		UserMonthlySummaryResponse response = userBusiness.getUserMonthlySummary(id, yearMonth);

		if (response == null) {
			throw new FinanceManagerException(myConfig.getGenericInternalServerErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
