package com.kamis.financemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kamis.financemanager.business.LoanBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.error.ErrorResponse;
import com.kamis.financemanager.rest.domain.generic.SuccessMessageResponse;
import com.kamis.financemanager.rest.domain.loans.LoanPostRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LoanController {

	@Autowired
	private LoanBusiness loanBusiness;

	@Autowired
	private YAMLConfig myConfig;

	@Operation(summary = "Create a new Loan for a user")
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
	@PostMapping("/users/{userId}/loans")
	public SuccessMessageResponse createLoan(
			@Parameter(description = "id of the user to create loan for") @PathVariable Integer userId,
			@RequestBody LoanPostRequest request) {
		log.info("creating loan for user with id {}", userId);

		if (userId == null) {
			throw new FinanceManagerException(myConfig.getUserIdRequiredError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		boolean success = loanBusiness.createLoan(request, userId);

		if (success) {
			return new SuccessMessageResponse(myConfig.getGenericSuccessMessage());
		} else {
			throw new FinanceManagerException(myConfig.getGenericInternalServerErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
