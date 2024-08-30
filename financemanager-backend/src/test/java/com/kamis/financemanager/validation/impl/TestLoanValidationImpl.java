package com.kamis.financemanager.validation.impl;

import com.kamis.financemanager.AppTestUtils;
import com.kamis.financemanager.business.LoanBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.LoanManualPayment;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.LoanRepository;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.loans.LoanRequest;
import com.kamis.financemanager.rest.domain.loans.ManualLoanPaymentRequest;
import com.kamis.financemanager.validation.LoanValidation;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertThrows;

@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestLoanValidationImpl {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");
    static String username = "loanvalidation_testuser";

    @Autowired
    private LoanBusiness loanBusiness;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanValidation loanValidation;

    @Autowired
    private YAMLConfig myConfig;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    @Transactional
    public void setUp() {

        loanRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.saveAndFlush(AppTestUtils.buildUserNoRole(username));

    }

    @Test
    void testValidateGetAllLoansRequest_userIdOnly() {
        loanValidation.validateGetAllLoansRequest(1, null, null);
    }

    @Test
    void testValidateGetAllLoansRequest_allFields() {
        loanValidation.validateGetAllLoansRequest(1, FinanceManagerConstants.LOAN_SORT_BY_NAME, null);
        loanValidation.validateGetAllLoansRequest(1, "", null);
    }

    @Test
    void testValidateGetAllLoansRequest_nullUsername() {
        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateGetAllLoansRequest(null, null, null));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getUserIdRequiredError()));

    }

    @Test
    void testValidateGetAllLoansRequest_invalidSortBy() {
        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateGetAllLoansRequest(null, "iaminvalid", null));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());

    }

    @Test
    void testValidateLoanRequest_valid(){
        LoanRequest request = AppTestUtils.mockLoanRequest("loan", 60, (float)0.05, PaymentFrequencyEnum.MONTHLY.getFrequency(), (float)1000, LocalDate.now());
        loanValidation.validateLoanRequest(request);
    }

    @Test
    void testValidateLoanRequest_null() {
        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(null));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));
    }

    @Test
    void testValidateLoanRequest_invalidName() {
        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(
                        AppTestUtils.mockLoanRequest(null, 60, (float)0.05, PaymentFrequencyEnum.MONTHLY.getFrequency(), (float)1000, LocalDate.now())));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));

        e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(
                        AppTestUtils.mockLoanRequest("", 60, (float)0.05, PaymentFrequencyEnum.MONTHLY.getFrequency(), (float)1000, LocalDate.now())));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));
    }

    @Test
    void testValidateLoanRequest_invalidTerm() {
        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(
                        AppTestUtils.mockLoanRequest("name", null, (float)0.05, PaymentFrequencyEnum.MONTHLY.getFrequency(), (float)1000, LocalDate.now())));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));

        e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(
                        AppTestUtils.mockLoanRequest("name", 0, (float)0.05, PaymentFrequencyEnum.MONTHLY.getFrequency(), (float)1000, LocalDate.now())));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));
    }

    @Test
    void testValidateLoanRequest_invalidRate() {
        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(
                        AppTestUtils.mockLoanRequest("name", 60, null, PaymentFrequencyEnum.MONTHLY.getFrequency(), (float)1000, LocalDate.now())));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));

        e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(
                        AppTestUtils.mockLoanRequest("name", 60, (float)-1, PaymentFrequencyEnum.MONTHLY.getFrequency(), (float)1000, LocalDate.now())));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));
    }

    @Test
    void testValidateLoanRequest_invalidFrequency() {
        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(
                        AppTestUtils.mockLoanRequest("name", 60, (float)0.05, null, (float)1000, LocalDate.now())));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));

        e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(
                        AppTestUtils.mockLoanRequest("name", 60, (float)0.05, "", (float)1000, LocalDate.now())));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));

        e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(
                        AppTestUtils.mockLoanRequest("name", 60, (float)0.05, "invalid_freq", (float)1000, LocalDate.now())));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));
    }

    @Test
    void testValidateLoanRequest_invalidPrincipal() {
        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(
                        AppTestUtils.mockLoanRequest("name", 60, (float)0.05, PaymentFrequencyEnum.MONTHLY.getFrequency(), null, LocalDate.now())));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));

        e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(
                        AppTestUtils.mockLoanRequest("name", 60, (float)0.05, PaymentFrequencyEnum.MONTHLY.getFrequency(), (float)0, LocalDate.now())));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));
    }

    @Test
    void testValidateLoanRequest_invalidFirstPaymentDate() {
        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateLoanRequest(
                        AppTestUtils.mockLoanRequest("name", 60, (float)0.05, PaymentFrequencyEnum.MONTHLY.getFrequency(), (float)1000, null)));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericUnprocessableError()));

    }


    @Test
    void testValidateCreatePaymentRequest_validToAddLoanAfterPaymentWithoutExpDate() {
        Optional<User> optUser = userRepository.findByUsername(username);
        assert(optUser.isPresent());

        LocalDate startDt = LocalDate.now();

        Loan loan = AppTestUtils.buildLoan(optUser.get(), "testLoan", (float)1000, (float)100, startDt);

        LoanManualPayment manualPayment = AppTestUtils.buildLoanManualPayment((float)500, startDt, null);

        loan.addManualLoanPayment(manualPayment);

        //Calculate required payments
        loan = loanBusiness.calculateLoanValues(loan);

        //Save Loan
        loan = loanRepository.saveAndFlush(loan);

        ManualLoanPaymentRequest request;

        //Scenario: Add new manual payment after previous payment start date when previous payment has no end date
        request = AppTestUtils.buildManualLoanPaymentRequest((float)500, startDt.plusWeeks(1), null);
        loanValidation.validateCreatePaymentRequest(loan.getId(), optUser.get().getId(), request);

    }

    @Test
    void testValidateCreatePaymentRequest_validToAddReqWithoutExpDateBeforeOtherPayment() {
        Optional<User> optUser = userRepository.findByUsername(username);
        assert(optUser.isPresent());

        LocalDate startDt = LocalDate.now();

        Loan loan = AppTestUtils.buildLoan(optUser.get(), "testLoan", (float)1000, (float)100, startDt);

        LoanManualPayment manualPayment = AppTestUtils.buildLoanManualPayment((float)500, startDt.plusWeeks(1), null);

        loan.addManualLoanPayment(manualPayment);

        //Calculate required payments
        loan = loanBusiness.calculateLoanValues(loan);

        //Save Loans
        loan = loanRepository.saveAndFlush(loan);

        ManualLoanPaymentRequest request;

        //Scenario: Add new manual payment before previous payment start date when request has no expiration date
        request = AppTestUtils.buildManualLoanPaymentRequest((float)500, startDt, null);
        loanValidation.validateCreatePaymentRequest(loan.getId(), optUser.get().getId(), request);

    }

    @Test
    void testValidateCreatePaymentRequest_validToAddDayAfterPreviousPaymentExpDate() {
        Optional<User> optUser = userRepository.findByUsername(username);
        assert(optUser.isPresent());

        LocalDate startDt = LocalDate.now();

        Loan loan = AppTestUtils.buildLoan(optUser.get(), "testLoan", (float)1000, (float)100, startDt);

        LoanManualPayment manualPayment = AppTestUtils.buildLoanManualPayment((float)500, startDt.plusWeeks(1), startDt.plusWeeks(2));

        loan.addManualLoanPayment(manualPayment);

        //Calculate required payments
        loan = loanBusiness.calculateLoanValues(loan);

        //Save Loans
        loan = loanRepository.saveAndFlush(loan);

        ManualLoanPaymentRequest request;

        //Scenario: Add new manual payment the day the previous payment ends
        request = AppTestUtils.buildManualLoanPaymentRequest((float)500, startDt.plusWeeks(2).plusDays(1), null);
        loanValidation.validateCreatePaymentRequest(loan.getId(), optUser.get().getId(), request);
    }

    @Test
    void testValidateCreatePaymentRequest_invalidToAdd_overlapsExistingPaymentWithNoExpDate() {
        Optional<User> optUser = userRepository.findByUsername(username);
        assert(optUser.isPresent());

        LocalDate startDt = LocalDate.now();

        Loan loan = AppTestUtils.buildLoan(optUser.get(), "testLoan", (float)1000, (float)100, startDt);

        LoanManualPayment manualPayment = AppTestUtils.buildLoanManualPayment((float)500, startDt.plusWeeks(1), null);

        loan.addManualLoanPayment(manualPayment);

        //Calculate required payments
        loan = loanBusiness.calculateLoanValues(loan);

        //Save Loans
        Loan finalLoan = loanRepository.saveAndFlush(loan);

        ManualLoanPaymentRequest request;

        //Scenario: Add new manual payment the day the previous payment ends
        request = AppTestUtils.buildManualLoanPaymentRequest((float)500, startDt, startDt.plusWeeks(2));

        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateCreatePaymentRequest(finalLoan.getId(), optUser.get().getId(), request));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getPaymentExistsError()));
    }

    @Test
    void testValidateCreatePaymentRequest_invalidToAdd_effDateOverlapsExistingPayment() {
        Optional<User> optUser = userRepository.findByUsername(username);
        assert(optUser.isPresent());

        LocalDate startDt = LocalDate.now();

        Loan loan = AppTestUtils.buildLoan(optUser.get(), "testLoan", (float)1000, (float)100, startDt);

        LoanManualPayment manualPayment = AppTestUtils.buildLoanManualPayment((float)500, startDt.plusWeeks(1), startDt.plusWeeks(3));

        loan.addManualLoanPayment(manualPayment);

        //Calculate required payments
        loan = loanBusiness.calculateLoanValues(loan);

        //Save Loans
        Loan finalLoan = loanRepository.saveAndFlush(loan);

        ManualLoanPaymentRequest request;

        //Scenario: Add new manual payment the day the previous payment ends
        request = AppTestUtils.buildManualLoanPaymentRequest((float)500, startDt.plusWeeks(2), null);

        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateCreatePaymentRequest(finalLoan.getId(), optUser.get().getId(), request));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getPaymentExistsError()));
    }

    @Test
    void testValidateCreatePaymentRequest_invalidToAdd_expDateOverlapsExistingPayment() {
        Optional<User> optUser = userRepository.findByUsername(username);
        assert(optUser.isPresent());

        LocalDate startDt = LocalDate.now();

        Loan loan = AppTestUtils.buildLoan(optUser.get(), "testLoan", (float)1000, (float)100, startDt);

        LoanManualPayment manualPayment = AppTestUtils.buildLoanManualPayment((float)500, startDt.plusWeeks(1), startDt.plusWeeks(3));

        loan.addManualLoanPayment(manualPayment);

        //Calculate required payments
        loan = loanBusiness.calculateLoanValues(loan);

        //Save Loans
        Loan finalLoan = loanRepository.saveAndFlush(loan);

        ManualLoanPaymentRequest request;

        //Scenario: Add new manual payment the day the previous payment ends
        request = AppTestUtils.buildManualLoanPaymentRequest((float)500, startDt, startDt.plusWeeks(2));

        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateCreatePaymentRequest(finalLoan.getId(), optUser.get().getId(), request));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getPaymentExistsError()));
    }

    @Test
    void testValidateCreatePaymentRequest_invalidToAdd_overlapsPaymentWithExpDate() {
        Optional<User> optUser = userRepository.findByUsername(username);
        assert(optUser.isPresent());

        LocalDate startDt = LocalDate.now();

        Loan loan = AppTestUtils.buildLoan(optUser.get(), "testLoan", (float)1000, (float)100, startDt);

        LoanManualPayment manualPayment = AppTestUtils.buildLoanManualPayment((float)500, startDt.plusWeeks(1), startDt.plusWeeks(3));

        loan.addManualLoanPayment(manualPayment);

        //Calculate required payments
        loan = loanBusiness.calculateLoanValues(loan);

        //Save Loans
        Loan finalLoan = loanRepository.saveAndFlush(loan);

        ManualLoanPaymentRequest request;

        //Scenario: Add new manual payment the day the previous payment ends
        request = AppTestUtils.buildManualLoanPaymentRequest((float)500, startDt.plusWeeks(2), null);

        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateCreatePaymentRequest(finalLoan.getId(), optUser.get().getId(), request));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getPaymentExistsError()));
    }

    @Test
    void testValidateCreatePaymentRequest_invalidToAdd_effDateIsSameAsPreviousExpDate() {
        Optional<User> optUser = userRepository.findByUsername(username);
        assert(optUser.isPresent());

        LocalDate startDt = LocalDate.now();

        Loan loan = AppTestUtils.buildLoan(optUser.get(), "testLoan", (float)1000, (float)100, startDt);

        LoanManualPayment manualPayment = AppTestUtils.buildLoanManualPayment((float)500, startDt.plusWeeks(1), startDt.plusWeeks(3));

        loan.addManualLoanPayment(manualPayment);

        //Calculate required payments
        loan = loanBusiness.calculateLoanValues(loan);

        //Save Loans
        Loan finalLoan = loanRepository.saveAndFlush(loan);

        ManualLoanPaymentRequest request;

        //Scenario: Add new manual payment the day the previous payment ends
        request = AppTestUtils.buildManualLoanPaymentRequest((float)500, startDt.plusWeeks(3), null);

        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateCreatePaymentRequest(finalLoan.getId(), optUser.get().getId(), request));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getPaymentExistsError()));
    }

    @Test
    void testValidateCreatePaymentRequest_loanDoesNotExist() {
        Optional<User> optUser = userRepository.findByUsername(username);
        assert(optUser.isPresent());

        //Scenario: Add new manual payment the day the previous payment ends
        ManualLoanPaymentRequest request = AppTestUtils.buildManualLoanPaymentRequest((float)500, LocalDate.now(), null);

        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateCreatePaymentRequest(999, optUser.get().getId(), request));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericNotFoundMessage()));

    }

    @Test
    void testValidateCreatePaymentRequest_incorrectUserId() {
        Optional<User> optUser = userRepository.findByUsername(username);
        assert(optUser.isPresent());

        Loan loan = AppTestUtils.buildLoan(optUser.get(), "testLoan", (float)1000, (float)100, LocalDate.now());
        loan = loanBusiness.calculateLoanValues(loan);
        loan = loanRepository.saveAndFlush(loan);

        //Scenario: Add new manual payment the day the previous payment ends
        ManualLoanPaymentRequest request = AppTestUtils.buildManualLoanPaymentRequest((float)500, LocalDate.now(), null);

        Loan finalLoan = loan;
        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateCreatePaymentRequest(finalLoan.getId(), 999, request));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getGenericNotFoundMessage()));

    }

    @Test
    void testValidateCreatePaymentRequest_amountMissing() {
        Optional<User> optUser = userRepository.findByUsername(username);
        assert(optUser.isPresent());

        Loan loan = AppTestUtils.buildLoan(optUser.get(), "testLoan", (float)1000, (float)100, LocalDate.now());
        loan = loanBusiness.calculateLoanValues(loan);
        loan = loanRepository.saveAndFlush(loan);

        //Scenario: Add new manual payment the day the previous payment ends
        ManualLoanPaymentRequest request = AppTestUtils.buildManualLoanPaymentRequest((float)500, LocalDate.now(), null);
        request.setAmount(null);

        Loan finalLoan = loan;
        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateCreatePaymentRequest(finalLoan.getId(), optUser.get().getId(), request));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getPaymentAmountRequiredError()));

    }

    @Test
    void testValidateCreatePaymentRequest_effectiveDateRequired() {
        Optional<User> optUser = userRepository.findByUsername(username);
        assert(optUser.isPresent());

        Loan loan = AppTestUtils.buildLoan(optUser.get(), "testLoan", (float)1000, (float)100, LocalDate.now());
        loan = loanBusiness.calculateLoanValues(loan);
        loan = loanRepository.saveAndFlush(loan);

        //Scenario: Add new manual payment the day the previous payment ends
        ManualLoanPaymentRequest request = AppTestUtils.buildManualLoanPaymentRequest((float)500, LocalDate.now(), null);
        request.setEffectiveDate(null);

        Loan finalLoan = loan;
        FinanceManagerException e = assertThrows(FinanceManagerException.class, () ->
                loanValidation.validateCreatePaymentRequest(finalLoan.getId(), optUser.get().getId(), request));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatusCode());
        assert(e.getMessage().equalsIgnoreCase(myConfig.getPaymentEffectiveDateRequiredError()));

    }
}
