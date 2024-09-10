package com.kamis.financemanager.controller;

import com.kamis.financemanager.business.BillBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.bills.BillPostRequest;
import com.kamis.financemanager.rest.domain.error.ErrorResponse;
import com.kamis.financemanager.rest.domain.generic.SuccessMessageResponse;
import com.kamis.financemanager.rest.domain.incomes.IncomePostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillController {

    @Autowired
    private YAMLConfig myConfig;

    @Autowired
    private BillBusiness billBusiness;

    @Operation(summary = "Create a new bill")
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
    @PostMapping("/users/{userId}/bills")
    public ResponseEntity<?> createBill(@PathVariable Integer userId, @RequestBody BillPostRequest request) {

        boolean success = billBusiness.createBill(userId, request);

        if (success) {
            SuccessMessageResponse response = new SuccessMessageResponse(myConfig.getIncomeCreatedMsg());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            throw new FinanceManagerException(myConfig.getGenericInternalServerErrorMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
