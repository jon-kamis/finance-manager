package com.kamis.financemanager.business.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.kamis.financemanager.business.TransactionBusiness;
import com.kamis.financemanager.database.domain.Transaction;
import com.kamis.financemanager.database.repository.TransactionRepository;
import com.kamis.financemanager.database.specifications.GenericSpecification;
import com.kamis.financemanager.database.specifications.QueryOperation;
import com.kamis.financemanager.enums.TransactionTypeEnum;
import com.kamis.financemanager.factory.TransactionFactory;
import com.kamis.financemanager.rest.domain.transactions.TransactionOccuranceResponse;
import com.kamis.financemanager.rest.domain.transactions.TransactionTotals;
import com.kamis.financemanager.rest.domain.users.UserMonthlySummaryResponse;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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
	public UserMonthlySummaryResponse getUserMonthlySummary(int id, String yearMonth) throws FinanceManagerException {

		UserMonthlySummaryResponse response = new UserMonthlySummaryResponse();
		List<TransactionOccuranceResponse> tOccurrences = new ArrayList<>();
		TransactionTotals totals = new TransactionTotals();

		Optional<User> u = userRepository.findById(id);

		if (u.isEmpty()) {
			throw new FinanceManagerException(myConfig.getGenericNotFoundMessage(), HttpStatus.NOT_FOUND);
		}

		//Get desired year and month
		int year;
		int month;

		try {
			String[] dateArr = Strings.split(yearMonth, "-");
			year = Integer.parseInt(dateArr[0]);
			month = Integer.parseInt(dateArr[1]);
		} catch (Exception e) {
			log.info("Caught exception when attempting to parse year and month. Throwing new bad request");
			throw new FinanceManagerException(myConfig.getYearMonthInvalidErrorMsg(), HttpStatus.BAD_REQUEST);
		}

		Date startDt = Date.from(LocalDate.of(year, month, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date endDt = Date.from(LocalDate.of(year, month + 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusNanos(1));

		GenericSpecification<Transaction> tSpec = new GenericSpecification<>();
		GenericSpecification<Transaction> effDateSpec = new GenericSpecification<>();
		GenericSpecification<Transaction> expDateSpec = new GenericSpecification<>();

		expDateSpec = expDateSpec.where("expirationDate", null, QueryOperation.IS_NULL)
				.or("expirationDate", startDt, QueryOperation.GREATER_THAN_EQUAL_TO_DATE);

		effDateSpec = effDateSpec.where("effectiveDate", endDt, QueryOperation.LESS_THAN_EQUAL_TO_DATE);
		tSpec = tSpec.where("userId", id, QueryOperation.EQUALS);

		List<Transaction> transactions = transactionRepository.findAll(Specification.where(tSpec.build()).and(effDateSpec.build().and(expDateSpec.build())));

		if (!transactions.isEmpty()) {
			List<Date> occurrences;
			for (Transaction t : transactions) {
				occurrences = transactionBusiness.getPaysInDateRange(t, startDt, endDt);
				tOccurrences.addAll(TransactionFactory.buildTransactionOccurrenceResponses(t, occurrences));

				switch (t.getCategory()) {
                    case LOAN:
						totals.totalLoanPayments += t.getAmount() * occurrences.size();
                        break;
                    case TAXES :
						totals.totalTax += t.getAmount() * occurrences.size();
						break;
					case PAYCHECK:
						totals.totalIncome += t.getAmount() * occurrences.size();
						break;
					case BENEFIT:
						if (t.getType() == TransactionTypeEnum.EXPENSE) {
							totals.totalMisc += t.getAmount() * occurrences.size();
						} else {
							totals.totalIncome += t.getAmount() * occurrences.size();
						}
						break;
					case BILL:
						totals.totalBills += t.getAmount() * occurrences.size();
						break;
				}
			}
		}

		response.setTransactions(tOccurrences);
		response.setTotals(totals);
		return response;
	}

}
