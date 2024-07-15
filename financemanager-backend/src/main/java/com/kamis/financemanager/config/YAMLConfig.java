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

	//Generic Messages
	private String genericInternalServerErrorMessage;
	private String genericMethodNotAllowedErrorMsg;
	private String jwtSecret;
	private String genericNotFoundErrorMsg;
	private String requiredFieldsBlankError;
	private String userIdRequiredError;
    private String genericSuccessMessage;
	
    //Loan messages
    private String invalidLoanPaymentFrequencyError;
    
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
}
