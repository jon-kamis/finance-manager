package com.kamis.financemanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@Data
public class YAMLConfig {

	//Configurations
	private boolean showGenericSpecificationBuilder;
	
	//Generic Messages
	private String genericAccessDeniedErrorMsg;
	private String genericInternalServerErrorMessage;
	private String genericNotFoundMessage;
	private String genericMethodNotAllowedErrorMsg;
	private String jwtSecret;
	private String genericNotFoundErrorMsg;
	private String requiredFieldsBlankError;
	private String userIdRequiredError;
    private String genericSuccessMessage;
    private String effectiveDateRequiredErrorMsg;
    private String invalidFrequencyErrorMsg;
	
    //Loan messages
    private String invalidLoanPaymentFrequencyError;
    
    //Transaction Messages
    private String invalidDayOfMonthErrorMsg;
    private String invalidAmountErrorMsg;
    private String atLeastOneDateRequiredErrorMsg;
    private String invalidCategoryErrorMsg;
    private String invalidTransactionTypeErrorMsg;
    private String transactionCreatedMsg;
    private String invalidTransactionSearchCategory;
    private String invalidTransactionSearchFrequency;
    private String invalidTransactionSearchType;
    
	//Registration Messages
	private String usernameExistsError;
	private String emailExistsError;
	private String invalidPasswordError;
	private String userRegisteredMessage;
	
	//Auth messages
	private String invalidCredentialsErrorMsg;
	
	//Role Constants
	private String defaultUserRole;
	
	//App info
	private String applicationUsername;
	
	//Pagination constants
	private String invalidPagingParameterErrorMsg;
	
	//Sorting constants
	private String invalidSortTypeErrorMsg;
	private String invalidSortByErrorMsg;
}
