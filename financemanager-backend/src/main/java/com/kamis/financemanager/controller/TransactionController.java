package com.kamis.financemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kamis.financemanager.business.TransactionBusiness;
import com.kamis.financemanager.rest.domain.error.ErrorResponse;
import com.kamis.financemanager.rest.domain.generic.SuccessMessageResponse;
import com.kamis.financemanager.rest.domain.transactions.PagedTransactionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class TransactionController {

	@Autowired
	private TransactionBusiness transactionBusiness;

	@Operation(summary = "Get all user transactions")
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
	@PreAuthorize("@securityService.hasAccess(authentication, #userId)")
	@GetMapping("/users/{userId}/transactions")
	public ResponseEntity<?> getAllUserTransactions(
			@Parameter(name = "userId", description = "userId", example = "1", required = true) @PathVariable Integer userId,
			@Parameter(name = "name", description = "Filter by transaction name", allowEmptyValue = true) @RequestParam(required = false) String name,
			@Parameter(name = "parent", description = "Filter by parent table name", schema = @Schema(allowableValues= {"incomes"})) @RequestParam(required = false) String parent,
			@Parameter(name = "category", description = "Filter by category", schema = @Schema(allowableValues = {
					"benefit", "bill", "loan", "paycheck" })) @RequestParam(required = false) String category,
			@Parameter(name = "type", description = "Filter by type", schema = @Schema(allowableValues = { "income",
					"expense" })) @RequestParam(required = false) String type,
			@Parameter(name = "sortBy", description = "Sort results. Default is 'name'", schema = @Schema(allowableValues = {"amount", "category", "name", "type"})) @RequestParam(required = false) String sortBy,
			@Parameter(name = "sortType", description = "Sort direction. Default is 'asc'", schema = @Schema(allowableValues = {"asc", "desc"})) @RequestParam(required = false) String sortType,
			@Parameter(name = "page", description = "Page of results") @RequestParam(required = false) Integer page,
			@Parameter(name = "pageSize", description = "Size of result pages to return") @RequestParam(required = false) Integer pageSize) {

		PagedTransactionResponse response = transactionBusiness.getAllUserTransactions(userId, name, parent, category, type, sortBy, sortType, page, pageSize);
		
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
}
