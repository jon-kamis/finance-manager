package com.kamis.financemanager;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.kamis.financemanager.database.domain.Role;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.LoanPaymentRepository;
import com.kamis.financemanager.database.repository.LoanRepository;
import com.kamis.financemanager.database.repository.RoleRepository;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.database.repository.UserRoleRepository;
import com.kamis.financemanager.exception.FinanceManagerException;

import io.restassured.RestAssured;
	
public class FinancemanagerApplicationTests {

	@Autowired
	private LoanPaymentRepository loanPaymentRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@LocalServerPort
	private Integer port;

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

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
		setUpWeb();
		setUpDb();
	}
	
	public void setUpDb() {
		
		Optional<Role> optAdminRole = roleRepository.findByName("admin");
		Optional<Role> optUserRole = roleRepository.findByName("user");
		
		if (optAdminRole.isEmpty()) {
			roleRepository.save(AppTestUtils.buildRole("admin"));
		}
		
		if (optUserRole.isEmpty()) {
			roleRepository.save(AppTestUtils.buildRole("user"));
		}
	}
	
	public void setUpWeb() {
		RestAssured.baseURI = "http://localhost:" + port;
	}
	
	public void populateUserData(String prefix) {
		Optional<Role> optAdminRole = roleRepository.findByName("admin");
		Optional<Role> optUserRole = roleRepository.findByName("user");
		
		if (optAdminRole.isEmpty() || optUserRole.isEmpty()) {
			throw new FinanceManagerException("Failed to retrieve loans during populate data", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		userRepository.save(AppTestUtils.buildUser(prefix+"_admin", optAdminRole.get()));
		userRepository.save(AppTestUtils.buildUser(prefix+"_user", optUserRole.get()));
	}
	
	public void cleanUserData(String prefix) {
		Optional<User> optAdmin = userRepository.findByUsername(prefix+"_admin");
		Optional<User> optUser = userRepository.findByUsername(prefix+"_user");
		
		if (optAdmin.isPresent()) {
			userRepository.delete(optAdmin.get());
		}
		
		if (optUser.isPresent()) {
			userRepository.delete(optUser.get());
		}
	}
	
	public void populateLoanTestData(String prefix) {
		
		
		Optional<User> optAdmin = userRepository.findByUsername(prefix+"_admin");
		Optional<User> optUser = userRepository.findByUsername(prefix+"_user");
		
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

}
