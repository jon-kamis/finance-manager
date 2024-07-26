package com.kamis.financemanager.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import com.kamis.financemanager.AppTestUtils;
import com.kamis.financemanager.business.TransactionBusiness;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.database.domain.Role;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.RoleRepository;
import com.kamis.financemanager.database.repository.TransactionRepository;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.database.repository.UserRoleRepository;
import com.kamis.financemanager.enums.TransactionCategoryEnum;
import com.kamis.financemanager.enums.TransactionTypeEnum;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.transactions.PagedTransactionResponse;
import com.kamis.financemanager.rest.domain.transactions.TransactionResponse;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class TestTransactionBusiness {

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private TransactionBusiness transactionBusiness;

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
		transactionRepository.deleteAll();
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

		User admin = optAdmin.get();
		User user = optUser.get();

		transactionRepository.save(BusinessTestUtils.mockBillTransaction(admin.getId(), "testBill"));
		transactionRepository.save(BusinessTestUtils.mockIncomeTransaction(admin.getId(), "testPaycheck"));
		transactionRepository.save(BusinessTestUtils.mockBenefitTransaction(admin.getId(), "testBenefit"));

		transactionRepository.save(BusinessTestUtils.mockBillTransaction(user.getId(), "testBill"));
		transactionRepository.save(BusinessTestUtils.mockIncomeTransaction(user.getId(), "testPaycheck"));
		transactionRepository.save(BusinessTestUtils.mockBenefitTransaction(user.getId(), "testBenefit"));

	}

	@Test
	@Transactional
	@WithMockUser(username = "admin", authorities = { "admin", "user" })
	public void testGetAllUserTransactionsNoFiltersOrPaging() {

		Optional<User> optAdmin = userRepository.findByUsername("admin");

		if (optAdmin.isEmpty()) {
			throw new FinanceManagerException("expected user data missing");
		}

		User admin = optAdmin.get();

		PagedTransactionResponse response = transactionBusiness.getAllUserTransactions(admin.getId(), null, null, null,
				null, null, null, null);

		assertEquals(3, response.getCount());
		assertEquals(3, response.getPageSize());
		assertEquals(1, response.getPage());
		assertEquals(3, response.getItems().size());

		response = transactionBusiness.getAllUserTransactions(admin.getId(), "", "", "", "", "", null, null);

		assertEquals(3, response.getCount());
		assertEquals(3, response.getPageSize());
		assertEquals(1, response.getPage());
		assertEquals(3, response.getItems().size());
	}

	@Test
	@Transactional
	@WithMockUser(username = "admin", authorities = { "admin", "user" })
	public void testGetAllUserTransactionsFilterByName() {

		Optional<User> optAdmin = userRepository.findByUsername("admin");

		if (optAdmin.isEmpty()) {
			throw new FinanceManagerException("expected user data missing");
		}

		User admin = optAdmin.get();

		PagedTransactionResponse response = transactionBusiness.getAllUserTransactions(admin.getId(), "test", null,
				null, null, null, null, null);

		assertEquals(3, response.getItems().size());

		response = transactionBusiness.getAllUserTransactions(admin.getId(), "testBill", null, null, null, null, null,
				null);

		assertEquals(1, response.getItems().size());
		assert (response.getItems().get(0).getName().equals("testBill"));
	}

	@Test
	@Transactional
	@WithMockUser(username = "admin", authorities = { "admin", "user" })
	public void testGetAllUserTransactionsFilterByCategory() {

		Optional<User> optAdmin = userRepository.findByUsername("admin");

		if (optAdmin.isEmpty()) {
			throw new FinanceManagerException("expected user data missing");
		}

		User admin = optAdmin.get();

		PagedTransactionResponse response = transactionBusiness.getAllUserTransactions(admin.getId(), null, "bill",
				null, null, null, null, null);

		assertEquals(1, response.getCount());
		assertEquals(1, response.getPageSize());
		assertEquals(1, response.getPage());
		assertEquals(1, response.getItems().size());
		assert (TransactionCategoryEnum.BILL.getCategory().equals(response.getItems().get(0).getCategory()));
	}

	@Test
	@Transactional
	@WithMockUser(username = "admin", authorities = { "admin", "user" })
	public void testGetAllUserTransactionsFilterByType() {

		Optional<User> optAdmin = userRepository.findByUsername("admin");

		if (optAdmin.isEmpty()) {
			throw new FinanceManagerException("expected user data missing");
		}

		User admin = optAdmin.get();

		PagedTransactionResponse response = transactionBusiness.getAllUserTransactions(admin.getId(), null, null,
				"expense", null, null, null, null);

		assertEquals(2, response.getCount());
		assertEquals(2, response.getPageSize());
		assertEquals(1, response.getPage());
		assertEquals(2, response.getItems().size());

		for (TransactionResponse tr : response.getItems()) {
			assert (TransactionTypeEnum.EXPENSE.getType().equals(tr.getType()));
		}
	}

	@Test
	@Transactional
	@WithMockUser(username = "admin", authorities = { "admin", "user" })
	public void testGetAllUserTransactionsSortBy() {

		Optional<User> optAdmin = userRepository.findByUsername("admin");

		if (optAdmin.isEmpty()) {
			throw new FinanceManagerException("expected user data missing");
		}

		User admin = optAdmin.get();
		PagedTransactionResponse response;

		// Default sort is name
		response = transactionBusiness.getAllUserTransactions(admin.getId(), null, null, null, null, null, null, null);

		for (int i = 0; i < response.getItems().size() - 1; i++) {
			log.info("Comparing Transactions no sortBy:");
			log.info("Transaction 1: {}", response.getItems().get(i).toString());
			log.info("Transaction 2: {}", response.getItems().get(i + 1).toString());
			assert (response.getItems().get(i).getName().compareTo(response.getItems().get(i + 1).getName()) <= 0);
		}

		// use name param
		response = transactionBusiness.getAllUserTransactions(admin.getId(), null, null, null, "name", null, null,
				null);

		for (int i = 0; i < response.getItems().size() - 1; i++) {
			log.info("Comparing Transactions sortBy name:");
			log.info("Transaction 1: {}", response.getItems().get(i).toString());
			log.info("Transaction 2: {}", response.getItems().get(i + 1).toString());
			assert (response.getItems().get(i).getName().compareTo(response.getItems().get(i + 1).getName()) <= 0);
		}

		// Sort by amount
		response = transactionBusiness.getAllUserTransactions(admin.getId(), null, null, null, "amount", null, null,
				null);

		for (int i = 0; i < response.getItems().size() - 1; i++) {
			log.info("Comparing Transactions sortBy amount:");
			log.info("Transaction 1: {}", response.getItems().get(i).toString());
			log.info("Transaction 2: {}", response.getItems().get(i + 1).toString());
			assert (response.getItems().get(i).getAmount() <= response.getItems().get(i + 1).getAmount());
		}
	}

	@Test
	@Transactional
	@WithMockUser(username = "admin", authorities = { "admin", "user" })
	public void testGetAllUserTransactionsSortType() {

		Optional<User> optAdmin = userRepository.findByUsername("admin");

		if (optAdmin.isEmpty()) {
			throw new FinanceManagerException("expected user data missing");
		}

		User admin = optAdmin.get();
		PagedTransactionResponse response;

		// Default sortType is ascending
		response = transactionBusiness.getAllUserTransactions(admin.getId(), null, null, null, null, null, null, null);

		for (int i = 0; i < response.getItems().size() - 1; i++) {
			log.info("Comparing Transactions no sort type:");
			log.info("Transaction 1: {}", response.getItems().get(i).toString());
			log.info("Transaction 2: {}", response.getItems().get(i + 1).toString());
			assert (response.getItems().get(i).getName().compareTo(response.getItems().get(i + 1).getName()) <= 0);
		}

		// set sort type to ascending
		response = transactionBusiness.getAllUserTransactions(admin.getId(), null, null, null, null,
				FinanceManagerConstants.SORT_TYPE_ASC, null, null);

		for (int i = 0; i < response.getItems().size() - 1; i++) {
			log.info("Comparing Transactions sort type asc:");
			log.info("Transaction 1: {}", response.getItems().get(i).toString());
			log.info("Transaction 2: {}", response.getItems().get(i + 1).toString());
			assert (response.getItems().get(i).getName().compareTo(response.getItems().get(i + 1).getName()) <= 0);
		}

		// set sort type to descending
		response = transactionBusiness.getAllUserTransactions(admin.getId(), null, null, null, null,
				FinanceManagerConstants.SORT_TYPE_DESC, null, null);

		for (int i = 0; i < response.getItems().size() - 1; i++) {
			log.info("Comparing Transactions sort type desc:");
			log.info("Transaction 1: {}", response.getItems().get(i).toString());
			log.info("Transaction 2: {}", response.getItems().get(i + 1).toString());
			assert (response.getItems().get(i).getName().compareTo(response.getItems().get(i + 1).getName()) >= 0);
		}

	}

	@Test
	@Transactional
	@WithMockUser(username = "admin", authorities = { "admin", "user" })
	public void testGetAllUserTransactionsPagination() {

		Optional<User> optAdmin = userRepository.findByUsername("admin");

		if (optAdmin.isEmpty()) {
			throw new FinanceManagerException("expected user data missing");
		}

		User admin = optAdmin.get();
		PagedTransactionResponse response;

		// Default pagination is everything
		response = transactionBusiness.getAllUserTransactions(admin.getId(), null, null, null, null, null, null, null);

		assertEquals(1, response.getPage());
		assertEquals(3, response.getPageSize());
		assertEquals(3, response.getCount());
		assertEquals(3, response.getItems().size());

		// Default test splitting results into 2 pages
		response = transactionBusiness.getAllUserTransactions(admin.getId(), null, null, null, null, null, 1, 2);

		assertEquals(1, response.getPage());
		assertEquals(2, response.getPageSize());
		assertEquals(3, response.getCount());
		assertEquals(2, response.getItems().size());

		// Second page
		response = transactionBusiness.getAllUserTransactions(admin.getId(), null, null, null, null, null, 2, 2);

		assertEquals(2, response.getPage());
		assertEquals(2, response.getPageSize());
		assertEquals(3, response.getCount());
		assertEquals(1, response.getItems().size());

	}

	@Test
	@WithMockUser(username = "admin", authorities = { "admin", "user" })
	public void testGetAllUserTransactionsAllParams() {

		Optional<User> optAdmin = userRepository.findByUsername("admin");

		if (optAdmin.isEmpty()) {
			throw new FinanceManagerException("expected user data missing");
		}

		User admin = optAdmin.get();
		PagedTransactionResponse response;

		transactionRepository.save(BusinessTestUtils.mockBenefitTransaction(admin.getId(), "special1",
				TransactionCategoryEnum.BENEFIT, TransactionTypeEnum.EXPENSE, (float) 1));
		transactionRepository.save(BusinessTestUtils.mockBenefitTransaction(admin.getId(), "special2",
				TransactionCategoryEnum.BENEFIT, TransactionTypeEnum.EXPENSE, (float) 2));
		transactionRepository.save(BusinessTestUtils.mockBenefitTransaction(admin.getId(), "special3",
				TransactionCategoryEnum.BENEFIT, TransactionTypeEnum.EXPENSE, (float) 3));
		transactionRepository.save(BusinessTestUtils.mockBenefitTransaction(admin.getId(), "special4",
				TransactionCategoryEnum.BENEFIT, TransactionTypeEnum.EXPENSE, (float) 4));
		transactionRepository.save(BusinessTestUtils.mockBenefitTransaction(admin.getId(), "special5",
				TransactionCategoryEnum.BENEFIT, TransactionTypeEnum.INCOME, (float) 1));
		transactionRepository.save(BusinessTestUtils.mockBenefitTransaction(admin.getId(), "special6",
				TransactionCategoryEnum.BILL, TransactionTypeEnum.EXPENSE, (float) 1));

		// Query with all filters present
		response = transactionBusiness.getAllUserTransactions(admin.getId(), "special",
				TransactionCategoryEnum.BENEFIT.getCategory(), TransactionTypeEnum.EXPENSE.getType(), "amount",
				FinanceManagerConstants.SORT_TYPE_DESC, 2, 2);
		
		assertEquals(2, response.getPage());
		assertEquals(2, response.getPageSize());
		assertEquals(2, response.getItems().size());
		assertEquals(4, response.getCount());
		
		for(int i = 0; i < response.getItems().size() - 1; i++) {
			log.info("Comparing Transactions sort type desc:");
			log.info("Transaction 1: {}", response.getItems().get(i).toString());
			log.info("Transaction 2: {}", response.getItems().get(i + 1).toString());
			assert (response.getItems().get(i).getAmount() >= response.getItems().get(i + 1).getAmount());
		}

	}
}