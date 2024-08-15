package com.kamis.financemanager.business.impl;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import com.kamis.financemanager.AppTestUtils;
import com.kamis.financemanager.business.LoanBusiness;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.LoanPayment;
import com.kamis.financemanager.database.domain.Role;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.LoanRepository;
import com.kamis.financemanager.database.repository.RoleRepository;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.database.repository.UserRoleRepository;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.loans.LoanPostRequest;
import com.kamis.financemanager.rest.domain.loans.LoanResponse;
import com.kamis.financemanager.rest.domain.loans.PagedLoanResponse;

import jakarta.transaction.Transactional;

@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestLoanBusinessImpl {

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

	@Mock
	private LoanBusinessImpl loanBusinessMock;

	@Autowired
	private LoanBusiness loanBusinessActual;

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private UserRepository userRepository;

	@BeforeAll
	static void beforeAll() {
		postgres.start();
	}

	@AfterAll
	static void afterAll() {
		postgres.stop();
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@BeforeEach
	@Transactional
	public void setUp() {

		userRoleRepository.deleteAll();
		roleRepository.deleteAll();
		loanRepository.deleteAll();
		userRepository.deleteAll();

		roleRepository.saveAndFlush(AppTestUtils.buildRole("admin"));
		roleRepository.saveAndFlush(AppTestUtils.buildRole("user"));

		Optional<Role> optAdminRole = roleRepository.findByName("admin");
		Optional<Role> optUserRole = roleRepository.findByName("user");

		if (optAdminRole.isEmpty() || optUserRole.isEmpty()) {
			throw new FinanceManagerException("Failed to retrieve loans during populate data",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		userRepository.saveAndFlush(AppTestUtils.buildUser("admin", optAdminRole.get()));
		userRepository.saveAndFlush(AppTestUtils.buildUser("user", optUserRole.get()));

		Optional<User> optAdmin = userRepository.findByUsername("admin");
		Optional<User> optUser = userRepository.findByUsername("user");

		if (optAdmin.isEmpty() || optUser.isEmpty()) {
			throw new FinanceManagerException("Failed to retrieve users during populate data",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		LocalDate date = LocalDate.now();

		loanRepository.saveAndFlush(AppTestUtils.buildLoan(optAdmin.get(), "loan1", 1000, 1000, date));
		loanRepository.saveAndFlush(AppTestUtils.buildLoan(optAdmin.get(), "loan2", 100, 100, date.plusWeeks(1)));
		loanRepository.saveAndFlush(AppTestUtils.buildLoan(optAdmin.get(), "loan3", 10, 10, date.plusWeeks(2)));

		loanRepository.saveAndFlush(AppTestUtils.buildLoan(optUser.get(), "loan1", 1000, 1000, date));
		loanRepository.saveAndFlush(AppTestUtils.buildLoan(optUser.get(), "loan2", 100, 100, date.plusWeeks(1)));
		loanRepository.saveAndFlush(AppTestUtils.buildLoan(optUser.get(), "loan3", 10, 10, date.plusWeeks(2)));

	}

	/**
	 * test that an active loan's balance is calculated correctly
	 */
	@Test
	public void testGetActiveLoanBalance() {

		Mockito.when(loanBusinessMock.getLoanBalance(any())).thenCallRealMethod();

		Loan loan = BusinessTestUtils.mockPaymentsForBalanceTestActive();

		float balance = loanBusinessMock.getLoanBalance(loan);

		assertEquals((float) 1, balance);

	}

	/**
	 * test that a paid loan's balance is calculated correctly
	 */
	@Test
	public void testGetFutureLoanBalance() {

		Mockito.when(loanBusinessMock.getLoanBalance(any())).thenCallRealMethod();

		Loan loan = BusinessTestUtils.mockPaymentsForBalanceTestFutureLoan();

		float balance = loanBusinessMock.getLoanBalance(loan);

		assertEquals(loan.getPrincipal(), balance);

	}

	/**
	 * test that null or empty loan payments list return 0 for balance
	 */
	@Test
	public void testGetNullOrEmptyPaymentsLoanBalance() {

		Mockito.when(loanBusinessMock.getLoanBalance(any())).thenCallRealMethod();

		Loan loan = new Loan();

		float balance = loanBusinessMock.getLoanBalance(null);
		assertEquals((float) 0, balance);

		balance = loanBusinessMock.getLoanBalance(loan);
		assertEquals((float) 0, balance);

		loan.setPayments(new ArrayList<>());

		balance = loanBusinessMock.getLoanBalance(loan);
		assertEquals((float) 0, balance);
	}

	@Test
	@Transactional
	public void testGetLoanById() {

		Optional<User> optUser = userRepository.findByUsername("user");
		if (optUser.isEmpty()) {
			throw new FinanceManagerException("user test data not populated", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		User user = optUser.get();

		List<Loan> loans = loanRepository.findByUserId(user.getId());
		Loan loan = loans.get(0);

		// Good Loan
		LoanResponse loanResponse = loanBusinessActual.getLoanById(user.getId(), loan.getId());

		assertNotNull(loanResponse);
		assertEquals(user.getId(), loanResponse.getUserId());
		assertEquals(loan.getId(), loanResponse.getId());

		// UserId is wrong
		loanResponse = loanBusinessActual.getLoanById(867, 1);
		assertNull(loanResponse);

		// Loan does not exist
		loanResponse = loanBusinessActual.getLoanById(1, 867);
		assertNull(loanResponse);

	}

	@Test
	@Transactional
	public void testGetUserLoans() {

		Optional<User> optUser = userRepository.findByUsername("user");
		if (optUser.isEmpty()) {
			throw new FinanceManagerException("user test data not populated", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		User user = optUser.get();
		PagedLoanResponse response;

		// Unfiltered, unpaged call should contain 3 loans
		response = loanBusinessActual.getUserLoans(user.getId(), null, null, null, null, null);

		assertEquals(3, response.getCount());
		assertEquals(3, response.getItems().size());
		assertEquals(1, response.getPage());
		assertEquals(3, response.getPageSize());
	}

	@Test
	@Transactional
	public void testGetUserLoansFiltered() {

		Optional<User> optUser = userRepository.findByUsername("user");
		if (optUser.isEmpty()) {
			throw new FinanceManagerException("user test data not populated", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		User user = optUser.get();
		PagedLoanResponse response;

		// null name filter should return everything
		response = loanBusinessActual.getUserLoans(user.getId(), null, null, null, null, null);

		assertEquals(3, response.getCount());
		assertEquals(3, response.getItems().size());

		// Filter by name 'loan' should contain 3 loans
		response = loanBusinessActual.getUserLoans(user.getId(), "loan", null, null, null, null);

		assertEquals(3, response.getCount());
		assertEquals(3, response.getItems().size());

		// Filter by name 'loan1' should contain 1 loan
		response = loanBusinessActual.getUserLoans(user.getId(), "loan1", null, null, null, null);

		assertEquals(1, response.getCount());
		assertEquals(1, response.getItems().size());

		// Blank name filter should return everything
		response = loanBusinessActual.getUserLoans(user.getId(), "", null, null, null, null);

		assertEquals(3, response.getCount());
		assertEquals(3, response.getItems().size());
	}

	@Test
	@Transactional
	public void testGetUserLoansPaged() {

		Optional<User> optUser = userRepository.findByUsername("user");
		if (optUser.isEmpty()) {
			throw new FinanceManagerException("user test data not populated", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		User user = optUser.get();
		PagedLoanResponse response;

		// There are 3 total loans

		// Request all user loans but page 1 with pageSize of 2
		response = loanBusinessActual.getUserLoans(user.getId(), null, null, null, 1, 2);

		assertEquals(1, response.getPage());
		assertEquals(2, response.getPageSize());
		assertEquals(3, response.getCount());
		assertEquals(2, response.getItems().size());

		// Request Page 2
		response = loanBusinessActual.getUserLoans(user.getId(), null, null, null, 2, 2);

		assertEquals(2, response.getPage());
		assertEquals(2, response.getPageSize());
		assertEquals(3, response.getCount());
		assertEquals(1, response.getItems().size());
	}

	@Test
	@Transactional
	public void testGetUserLoansSortBy() {

		Optional<User> optUser = userRepository.findByUsername("user");
		if (optUser.isEmpty()) {
			throw new FinanceManagerException("user test data not populated", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		User user = optUser.get();
		PagedLoanResponse response;

		// Sort by name, null sortType should default to ascending
		response = loanBusinessActual.getUserLoans(user.getId(), null, FinanceManagerConstants.LOAN_SORT_BY_NAME, null,
				null, null);

		assertEquals(3, response.getItems().size());
		assert (response.getItems().get(0).getName().compareTo(response.getItems().get(1).getName()) <= 0);
		assert (response.getItems().get(1).getName().compareTo(response.getItems().get(2).getName()) <= 0);

		// Sort by balance, null sortType should default to ascending
		response = loanBusinessActual.getUserLoans(user.getId(), null, FinanceManagerConstants.LOAN_SORT_BY_BALANCE,
				null, null, null);

		assertEquals(3, response.getItems().size());
		assert (response.getItems().get(0).getBalance() <= response.getItems().get(1).getBalance());
		assert (response.getItems().get(1).getBalance() <= response.getItems().get(2).getBalance());

		// Sort by firstPaymentDate, null sortType should default to ascending
		response = loanBusinessActual.getUserLoans(user.getId(), null, "name", null, null, null);

		assertEquals(3, response.getItems().size());
		assert (response.getItems().get(0).getFirstPaymentDate()
				.compareTo(response.getItems().get(1).getFirstPaymentDate()) <= 0);
		assert (response.getItems().get(1).getFirstPaymentDate()
				.compareTo(response.getItems().get(2).getFirstPaymentDate()) <= 0);

		// Invalid sort by should throw a 422 exception
		FinanceManagerException e = assertThrows(FinanceManagerException.class, () -> {
			loanBusinessActual.getUserLoans(user.getId(), null, "ThisIsInvalid", null, null, null);
		});

		assertEquals(400, e.getStatusCode().value());
	}

	@Test
	@Transactional
	public void testGetUserLoansSortType() {

		Optional<User> optUser = userRepository.findByUsername("user");
		if (optUser.isEmpty()) {
			throw new FinanceManagerException("user test data not populated", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		User user = optUser.get();
		PagedLoanResponse response;

		// Sort by name null sortType should default to ascending
		response = loanBusinessActual.getUserLoans(user.getId(), null, "name", null, null, null);

		assertEquals(3, response.getItems().size());
		assert (response.getItems().get(0).getName().compareTo(response.getItems().get(1).getName()) <= 0);
		assert (response.getItems().get(1).getName().compareTo(response.getItems().get(2).getName()) <= 0);

		// Sort by name blank sortType should default to ascending
		response = loanBusinessActual.getUserLoans(user.getId(), null, "name", "", null, null);

		assertEquals(3, response.getItems().size());
		assert (response.getItems().get(0).getName().compareTo(response.getItems().get(1).getName()) <= 0);
		assert (response.getItems().get(1).getName().compareTo(response.getItems().get(2).getName()) <= 0);

		// Sort by name ascending sortType
		response = loanBusinessActual.getUserLoans(user.getId(), null, "name", "asc", null, null);

		assertEquals(3, response.getItems().size());
		assert (response.getItems().get(0).getName().compareTo(response.getItems().get(1).getName()) <= 0);
		assert (response.getItems().get(1).getName().compareTo(response.getItems().get(2).getName()) <= 0);

		// Sort by name descending sortType
		response = loanBusinessActual.getUserLoans(user.getId(), null, "name", "desc", null, null);

		assertEquals(3, response.getItems().size());
		assert (response.getItems().get(0).getName().compareTo(response.getItems().get(1).getName()) >= 0);
		assert (response.getItems().get(1).getName().compareTo(response.getItems().get(2).getName()) >= 0);

		// Invalid sort by should throw a 400 exception
		FinanceManagerException e = assertThrows(FinanceManagerException.class, () -> {
			loanBusinessActual.getUserLoans(user.getId(), null, "name", "invalid", null, null);
		});

		assertEquals(400, e.getStatusCode().value());
	}

	@Test
	@Transactional
	public void testGetUserLoansSortPageAndFilter() {

		Optional<User> optUser = userRepository.findByUsername("user");
		if (optUser.isEmpty()) {
			throw new FinanceManagerException("user test data not populated", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		User user = optUser.get();
		PagedLoanResponse response;

		// Test sorting and paging put together
		response = loanBusinessActual.getUserLoans(user.getId(), "loan1", "name", "asc", 1, 10);

		assertEquals(1, response.getPage());
		assertEquals(10, response.getPageSize());
		assertEquals(1, response.getCount());
		assertEquals(1, response.getItems().size());

		// update filter to test sorting
		// Test sorting and paging put together
		response = loanBusinessActual.getUserLoans(user.getId(), "loan", "name", "asc", 1, 10);
		assert (response.getItems().get(0).getName().compareTo(response.getItems().get(1).getName()) <= 0);
		assert (response.getItems().get(1).getName().compareTo(response.getItems().get(2).getName()) <= 0);
	}
	
	@Test
	public void testLoanPaymentCalcMonthly() {
		
		Loan loan = new Loan();
		loan.setPrincipal((float)60000);
		loan.setFrequency(PaymentFrequencyEnum.MONTHLY);
		loan.setRate((float)0.045);
		loan.setTerm(60);
		
		loan = loanBusinessActual.calculateLoanPament(loan);
		assertEquals((float)1118.59, loan.getPayment());

	}
	
	@Test
	public void testLoanPaymentCalcBiWeekly() {
		
		Loan loan = new Loan();
		loan.setPrincipal((float)60000);
		loan.setFrequency(PaymentFrequencyEnum.BIWEEKLY);
		loan.setRate((float)0.045);
		loan.setTerm(60);
		
		loan = loanBusinessActual.calculateLoanPament(loan);
		assertEquals((float)515.8, loan.getPayment());

	}
	
	@Test
	public void testLoanPaymentCalcWeekly() {
		
		Loan loan = new Loan();
		loan.setPrincipal((float)60000);
		loan.setFrequency(PaymentFrequencyEnum.WEEKLY);
		loan.setRate((float)0.045);
		loan.setTerm(60);
		
		loan = loanBusinessActual.calculateLoanPament(loan);
		assertEquals((float)257.81, loan.getPayment());

	}
	
	@Test
	public void testCalculatePaymentSchedule() {
		
		Loan loan = new Loan();
		loan.setPrincipal((float)60000);
		loan.setFrequency(PaymentFrequencyEnum.MONTHLY);
		loan.setFirstPaymentDate(new Date());
		loan.setRate((float)0.045);
		loan.setTerm(60);
		loan.setPayment((float)1118.59);
		
		loan = loanBusinessActual.calculatePaymentSchedule(loan);
		
		assertEquals(60, loan.getPayments().size());
		
		LoanPayment firstPayment = loan.getPayments().get(0);
		LoanPayment secondPayment = loan.getPayments().get(1);
		LoanPayment lastPayment = loan.getPayments().get(loan.getPayments().size()-1);

		assertEquals((float)60000, lastPayment.getPrincipalToDate());
		assertEquals(firstPayment.getPrincipal() + secondPayment.getPrincipal(), secondPayment.getPrincipalToDate());
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", authorities = { "admin", "user" })
	public void testCreateLoan() {
		Optional<User> optUser = userRepository.findByUsername("user");
		if (optUser.isEmpty()) {
			throw new FinanceManagerException("user test data not populated", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		User user = optUser.get();
		String loanName = "testLoanFor_testCreateLoan";
		
		LoanPostRequest request = new LoanPostRequest();
		request.setFirstPaymentDate(new Date());
		request.setFrequency(PaymentFrequencyEnum.MONTHLY.getFrequency());
		request.setName(loanName);
		request.setPrincipal((float)60000);
		request.setRate((float)0.045);
		request.setTerm(60);
		
		loanBusinessActual.createLoan(request, user.getId());
		
		Optional<Loan> optLoan = loanRepository.getLoanByUserIdAndName(user.getId(), loanName);
		
		assert(optLoan.isPresent());
		
		Loan loan = optLoan.get();
		
		assertEquals((float)60000, loan.getPrincipal());
		assertEquals((float)1118.59, loan.getPayment());
		assertEquals(60, loan.getPayments().size());
	}
}
