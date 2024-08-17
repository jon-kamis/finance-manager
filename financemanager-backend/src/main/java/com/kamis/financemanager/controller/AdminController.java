package com.kamis.financemanager.controller;

import com.kamis.financemanager.business.LoanBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.rest.domain.auth.JwtResponse;
import com.kamis.financemanager.rest.domain.error.ErrorResponse;
import com.kamis.financemanager.rest.domain.generic.SuccessMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private LoanBusiness loanBusiness;

    @Autowired
    private YAMLConfig myConfig;

    @Operation(summary = "Trigger loan balance recalculation")
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
    @PostMapping("/update-loans")
    public ResponseEntity<?> updateLoanBalances() {

        loanBusiness.updateLoanBalancesAsync();

        return new ResponseEntity<>(new SuccessMessageResponse(myConfig.getGenericSuccessMessage()), HttpStatus.ACCEPTED);
    }
}
