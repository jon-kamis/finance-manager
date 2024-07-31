package com.kamis.financemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kamis.financemanager.business.IncomeBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.error.ErrorResponse;
import com.kamis.financemanager.rest.domain.generic.SuccessMessageResponse;
import com.kamis.financemanager.rest.domain.incomes.IncomePostRequest;
import com.kamis.financemanager.rest.domain.incomes.IncomeResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class IncomeController {

	@Autowired
	private YAMLConfig myConfig;
	
	@Autowired
	private IncomeBusiness incomeBusiness;
	
	@Operation(summary = "Create a new income")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "CREATED", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = SuccessMessageResponse.class)) }),
			@ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "403", description = "FORBIDDEN", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "404", description = "NOT_FOUND", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
	@PreAuthorize("@securityService.isOwner(authentication, #userId)")
	@PostMapping("/users/{userId}/incomes")
	public ResponseEntity<?> createIncome(@PathVariable Integer userId, @RequestBody IncomePostRequest request) {

		boolean success = incomeBusiness.createIncome(userId, request);

		if (success) {
			SuccessMessageResponse response = new SuccessMessageResponse(myConfig.getTransactionCreatedMsg());
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} else {
			throw new FinanceManagerException(myConfig.getGenericInternalServerErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@Operation(summary = "Get income by Id")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "CREATED", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = IncomeResponse.class)) }),
			@ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "403", description = "FORBIDDEN", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "404", description = "NOT_FOUND", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
	@PreAuthorize("@securityService.isOwner(authentication, #userId)")
	@GetMapping("/users/{userId}/incomes/{id}")
	public ResponseEntity<?> getIncomeById(@PathVariable Integer userId, @PathVariable Integer id) {

		IncomeResponse response = incomeBusiness.findByUserIdAndId(userId, id);

		if (response != null) {
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			throw new FinanceManagerException(myConfig.getGenericNotFoundErrorMsg(),
					HttpStatus.NOT_FOUND);
		}

	}
}
