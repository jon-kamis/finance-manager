package com.kamis.financemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kamis.financemanager.business.LoanBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.error.ErrorResponse;
import com.kamis.financemanager.rest.domain.generic.SuccessMessageResponse;
import com.kamis.financemanager.rest.domain.loans.LoanPostRequest;
import com.kamis.financemanager.rest.domain.loans.PagedLoanResponse;

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
	
	@Operation(summary = "Get all loans for a user")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "CREATED", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = PagedLoanResponse.class)) }),
			@ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "403", description = "FORBIDDEN", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "404", description = "NOT_FOUND", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
	@PreAuthorize("@securityService.hasAccess(authentication, #userId)")
	@GetMapping("/users/{userId}/loans")
	public ResponseEntity<?> getLoans(
			@Parameter(description = "id of the user to fetch loan for") @PathVariable Integer userId,
			@Parameter(description = "Search by loan name", allowEmptyValue = true) @RequestParam(required = false) String name,
			@Parameter(description = "Sort by field. Allowable values are 'balance', 'name', 'age'") @RequestParam(required = false) String sortBy,
			@Parameter(description = "Sort direction. Allowable values are 'desc', 'asc'") @RequestParam(required = false) String sortType,
			@Parameter(description = "Page of results") @RequestParam(required = false) Integer page,
			@Parameter(description = "Size of result pages to return") @RequestParam(required = false) Integer pageSize) {
		log.info("creating loan for user with id {}", userId);

		if (userId == null) {
			throw new FinanceManagerException(myConfig.getUserIdRequiredError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		PagedLoanResponse response = loanBusiness.getUserLoans(userId, name, sortBy, sortType, page, pageSize);

		if (response != null) {
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			throw new FinanceManagerException(myConfig.getGenericInternalServerErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}