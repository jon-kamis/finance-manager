package com.kamis.financemanager.business.impl;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.ArrayList;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import com.kamis.financemanager.AppTestUtils;
import com.kamis.financemanager.business.LoanBusiness;
import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.Role;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.LoanRepository;
import com.kamis.financemanager.database.repository.RoleRepository;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.database.repository.UserRoleRepository;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.loans.LoanResponse;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestLoanBusinessImpl {

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

	@Mock
	private LoanBusinessImpl loanBusinessMock;

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private LoanBusiness loanBusinessActual;

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
	public void setUp() {

		userRoleRepository.deleteAll();
		roleRepository.deleteAll();
		loanRepository.deleteAll();
		userRepository.deleteAll();
		
		roleRepository.save(AppTestUtils.buildRole("admin"));
		roleRepository.save(AppTestUtils.buildRole("user"));

		Optional<Role> optAdminRole = roleRepository.findByName("admin");
		Optional<Role> optUserRole = roleRepository.findByName("user");

		
		if (optAdminRole.isEmpty() || optUserRole.isEmpty()) {
			throw new FinanceManagerException("Failed to retrieve loans during populate data",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		userRepository.save(AppTestUtils.buildUser("admin", optAdminRole.get()));
		userRepository.save(AppTestUtils.buildUser("user", optUserRole.get()));
	
		Optional<User> optAdmin = userRepository.findByUsername("admin");
		Optional<User> optUser = userRepository.findByUsername("user");
		
		if (optAdmin.isEmpty() || optUser.isEmpty()) {
			throw new FinanceManagerException("Failed to retrieve users during populate data", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		LocalDate date = LocalDate.now();
		
		loanRepository.save(AppTestUtils.buildLoan(optAdmin.get(), "loan1", 1000, 1000, date));
		loanRepository.save(AppTestUtils.buildLoan(optAdmin.get(), "loan2", 100, 100, date.plusWeeks(1)));
		loanRepository.save(AppTestUtils.buildLoan(optAdmin.get(), "loan3", 10, 10, date.plusWeeks(2)));
		
		loanRepository.save(AppTestUtils.buildLoan(optUser.get(), "loan1", 1000, 1000, date));
		loanRepository.save(AppTestUtils.buildLoan(optUser.get(), "loan2", 100, 100, date.plusWeeks(1)));
		loanRepository.save(AppTestUtils.buildLoan(optUser.get(), "loan3", 10, 10, date.plusWeeks(2)));
		
	}

	/**
	 * test that an active loan's balance is calculated correctly
	 */
	@Test
	public void testGetActiveLoanBalance() {

		Mockito.when(loanBusinessMock.getLoanBalance(any())).thenCallRealMethod();

		Loan loan = TestUtils.mockPaymentsForBalanceTestActive();

		float balance = loanBusinessMock.getLoanBalance(loan);

		assertEquals((float) 1, balance);

	}

	/**
	 * test that a paid loan's balance is calculated correctly
	 */
	@Test
	public void testGetFutureLoanBalance() {

		Mockito.when(loanBusinessMock.getLoanBalance(any())).thenCallRealMethod();

		Loan loan = TestUtils.mockPaymentsForBalanceTestFutureLoan();

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

		List<Loan> allLoans = loanRepository.findAll();
		for (Loan l : allLoans) {
			log.info("Loan: {}", l.toString());
		}
		
		Optional<User> user = userRepository.findByUsername("user");

		if (user.isEmpty()) {
			throw new FinanceManagerException("user test data not populated", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		log.info("User Loans: {}", user.get().getLoans());

		Loan loan = user.get().getLoans().get(0);

		// Good Loan
		LoanResponse loanResponse = loanBusinessActual.getLoanById(user.get().getId(), loan.getId());

		assertNotNull(loanResponse);
		assertEquals(user.get().getId(), loanResponse.getUserId());
		assertEquals(loan.getId(), loanResponse.getId());

		// UserId is wrong
		loanResponse = loanBusinessActual.getLoanById(867, 1);
		assertNull(loanResponse);

		// Loan does not exist
		loanResponse = loanBusinessActual.getLoanById(1, 867);
		assertNull(loanResponse);

	}
}
