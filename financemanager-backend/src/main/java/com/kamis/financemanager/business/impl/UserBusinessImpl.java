package com.kamis.financemanager.business.impl;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

import com.kamis.financemanager.business.TransactionBusiness;
import com.kamis.financemanager.database.domain.Transaction;
import com.kamis.financemanager.database.repository.TransactionRepository;
import com.kamis.financemanager.enums.TransactionTypeEnum;
import com.kamis.financemanager.factory.TransactionFactory;
import com.kamis.financemanager.rest.domain.transactions.TransactionExpenseTotals;
import com.kamis.financemanager.rest.domain.transactions.TransactionIncomeTotals;
import com.kamis.financemanager.rest.domain.transactions.TransactionOccuranceResponse;
import com.kamis.financemanager.rest.domain.users.UserMonthlySummaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.business.UserBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.database.domain.Role;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.RoleRepository;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.factory.UserFactory;
import com.kamis.financemanager.rest.domain.auth.RegistrationRequest;
import com.kamis.financemanager.rest.domain.users.UserResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserBusinessImpl implements UserBusiness {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private TransactionBusiness transactionBusiness;

	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private YAMLConfig myConfig;

	@Override
	public UserResponse getUserById(int id) {
		Optional<User> optUser = userRepository.findById(id);

		//Throw exception if user was not found
		if (optUser.isEmpty()) {
			throw new FinanceManagerException(myConfig.getGenericNotFoundErrorMsg(), HttpStatus.NOT_FOUND);
		}
		
		return UserFactory.buildUserResponse(optUser.get());
	}

	@Override
	public boolean registerUser(RegistrationRequest request) throws FinanceManagerException {
		
		//Validate that all fields are populated
		if (request.getUsername().isBlank() || request.getPassword().isBlank() || request.getEmail().isBlank()
				|| request.getFirstName().isBlank() || request.getLastName().isBlank()) {
			throw new FinanceManagerException(myConfig.getRequiredFieldsBlankError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		//Validate Username and email are available
		boolean usernameExists = userRepository.countByUsername(request.getUsername()) > 0;
		boolean emailExists = userRepository.countByUsername(request.getUsername()) > 0;

		if (usernameExists) {
			throw new FinanceManagerException(myConfig.getUsernameExistsError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		if (emailExists) {
			throw new FinanceManagerException(myConfig.getEmailExistsError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		//Validate Password contains at least 1 letter
		if(!request.getPassword().matches(".*[a-zA-Z]+.*")) {
			throw new FinanceManagerException(myConfig.getInvalidPasswordError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		//Encode password
		request.setPassword(encoder.encode(request.getPassword()));
		
		//Build User
		User user = UserFactory.buildUserForRegistration(request);
		
		//Fetch default user role
		Optional<Role> role = roleRepository.findByName(myConfig.getDefaultUserRole());
		
		if (role.isEmpty()) {
			log.error("Default user role {} does not exist", myConfig.getDefaultUserRole());
			throw new FinanceManagerException(myConfig.getGenericInternalServerErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		//Add default user role
		user.addRole(role.get(), myConfig.getApplicationUsername());

        userRepository.saveAndFlush(user);
        return true;
	}

	@Override
	public UserMonthlySummaryResponse getUserMonthlySummary(int id, String yearStr, String monthStr) throws FinanceManagerException {

		UserMonthlySummaryResponse response = new UserMonthlySummaryResponse();
		List<TransactionOccuranceResponse> tOccurrences = new ArrayList<>();
		TransactionIncomeTotals incomeTotals = new TransactionIncomeTotals();
		TransactionExpenseTotals expenseTotals = new TransactionExpenseTotals();

		Optional<User> u = userRepository.findById(id);

		if (u.isEmpty()) {
			throw new FinanceManagerException(myConfig.getGenericNotFoundMessage(), HttpStatus.NOT_FOUND);
		}

		//Get desired year and month
		int year;
		int month;

		if (yearStr == null || yearStr.isBlank()) {
			year = LocalDate.now().getYear();
		} else {
			try {
				year = Integer.parseInt(yearStr);
			} catch (Exception e) {
				log.info("Caught exception when attempting to parse year. Throwing new bad request");
				throw new FinanceManagerException(myConfig.getYearMonthInvalidErrorMsg(), HttpStatus.BAD_REQUEST);
			}
		}

		if (monthStr == null || monthStr.isBlank()) {
			month = LocalDate.now().getMonth().getValue();
		} else {
			try {
				month = Integer.parseInt(monthStr);
			} catch (Exception e) {
				log.info("Caught exception when attempting to parse month. Throwing new bad request");
				throw new FinanceManagerException(myConfig.getYearMonthInvalidErrorMsg(), HttpStatus.BAD_REQUEST);
			}
		}

		Date startDt = Date.from(LocalDate.of(year, month, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date endDt = Date.from(LocalDate.of(year, month + 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusNanos(1));
		List<Transaction> transactions = transactionBusiness.getTransactionsForDateRange(id, startDt, endDt, null, null, null, null, null, null, null, null);

		if (!transactions.isEmpty()) {
			List<Date> occurrences;
			for (Transaction t : transactions) {
				occurrences = transactionBusiness.getPaysInDateRange(t, startDt, endDt);
				tOccurrences.addAll(TransactionFactory.buildTransactionOccurrenceResponses(t, occurrences));

				switch (t.getCategory()) {
                    case LOAN:
						expenseTotals.totalExpense += t.getAmount() * occurrences.size();
						expenseTotals.totalLoanPayments += t.getAmount() * occurrences.size();
                        break;
                    case TAXES :
						expenseTotals.totalExpense += t.getAmount() * occurrences.size();
						expenseTotals.totalTax += t.getAmount() * occurrences.size();
						break;
					case PAYCHECK:
						incomeTotals.grossTotal += t.getAmount() * occurrences.size();
						incomeTotals.totalPaycheck += t.getAmount() * occurrences.size();
						break;
					case BENEFIT:
						if (t.getType() == TransactionTypeEnum.EXPENSE) {
							expenseTotals.totalExpense += t.getAmount() * occurrences.size();
							expenseTotals.totalBenefit += t.getAmount() * occurrences.size();
						} else {
							incomeTotals.grossTotal += t.getAmount() * occurrences.size();
							incomeTotals.totalBenefit += t.getAmount() * occurrences.size();
						}
						break;
					case BILL:
						expenseTotals.totalBills += t.getAmount() * occurrences.size();
						expenseTotals.totalExpense += t.getAmount() * occurrences.size();
						break;
				}
			}
		}

		response.setMonth(Month.of(month).getDisplayName(TextStyle.FULL, Locale.US));
		incomeTotals.setNetTotal(incomeTotals.grossTotal - expenseTotals.totalExpense);

		response.setIncomeTotals(incomeTotals);
		response.setExpenseTotals(expenseTotals);
		response.setTransactions(tOccurrences);
		return response;
	}
}
