package com.kamis.financemanager.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.transactions.TransactionPostRequest;

@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
public class TestTransactionValidationImpl {

	@InjectMocks
	private TransactionValidationImpl transactionValidation;

	@Mock
	private YAMLConfig myConfig;

	@BeforeEach
	public void setMocks() {
		Mockito.lenient().when(myConfig.getUserIdRequiredError()).thenReturn(USER_REQ_ERR_MSG);
		Mockito.lenient().when(myConfig.getInvalidAmountErrorMsg()).thenReturn(INVALID_AMNT_ERR);
		Mockito.lenient().when(myConfig.getAtLeastOneDateRequiredErrorMsg()).thenReturn(AT_LEAST_ONE_DATE_ERR);
		Mockito.lenient().when(myConfig.getInvalidDayOfMonthErrorMsg()).thenReturn(INVALID_DAY_ERR);
		Mockito.lenient().when(myConfig.getEffectiveDateRequiredErrorMsg()).thenReturn(EFF_DATE_REQ_ERR);
		Mockito.lenient().when(myConfig.getInvalidCategoryErrorMsg()).thenReturn(INV_CAT_ERR);
		Mockito.lenient().when(myConfig.getInvalidFrequencyErrorMsg()).thenReturn(INV_FREQ_ERR);
		Mockito.lenient().when(myConfig.getInvalidTransactionTypeErrorMsg()).thenReturn(INV_TYPE_ERR);
		Mockito.lenient().when(myConfig.getInvalidTransactionSearchParentName()).thenReturn(INV_PARENT_TABLE_NAME_ERR);

		Mockito.lenient().when(myConfig.getInvalidTransactionSearchCategory()).thenReturn(INV_SEARCH_CAT_ERR);
		Mockito.lenient().when(myConfig.getInvalidTransactionSearchType()).thenReturn(INV_SEARCH_TYPE_ERR);
		Mockito.lenient().when(myConfig.getInvalidSortTypeErrorMsg()).thenReturn(INV_SORT_TYPE_ERR);
		Mockito.lenient().when(myConfig.getInvalidSortByErrorMsg()).thenReturn(INV_SORT_BY_ERR);
		Mockito.lenient().when(myConfig.getInvalidPagingParameterErrorMsg()).thenReturn(INV_PAGING_ERR);

	}

	private static final String INV_PARENT_TABLE_NAME_ERR  = "invalid parent type";
	private static final String USER_REQ_ERR_MSG = "user required";
	private static final String INVALID_AMNT_ERR = "invalid amount";
	private static final String AT_LEAST_ONE_DATE_ERR = "at least one date is required";
	private static final String INVALID_DAY_ERR = "invalid day of month";
	private static final String EFF_DATE_REQ_ERR = "effective date is required";
	private static final String INV_CAT_ERR = "invalid category";
	private static final String INV_FREQ_ERR = "invalid frequency";
	private static final String INV_TYPE_ERR = "invalid type";
	private static final String INV_SEARCH_CAT_ERR = "invalid search category";
	private static final String INV_SEARCH_TYPE_ERR = "invalid search type";
	private static final String INV_SORT_TYPE_ERR = "invalid sort type";
	private static final String INV_SORT_BY_ERR = "invalid sort option";
	private static final String INV_PAGING_ERR = "invalid paging option";

	@Test
	public void testValidateTransactionPostRequest_NullUser() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(null, new TransactionPostRequest("testTransaction",
					"income", "bill", "monthly", (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getUserIdRequiredError()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_InvalidUser() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(-3, new TransactionPostRequest("testTransaction",
					"income", "bill", "monthly", (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getUserIdRequiredError()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_NullAmount() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", "bill", "monthly", null, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidAmountErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_InvalidAmount() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", "bill", "monthly", (float) -1, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidAmountErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_NullDays() {
		FinanceManagerException e;

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", "bill", "monthly", (float) 50, null, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getAtLeastOneDateRequiredErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_EmptyDays() {
		FinanceManagerException e;

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", "bill", "monthly", (float) 50, new ArrayList<>(), new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getAtLeastOneDateRequiredErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_InvalidDayBelowZero() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(0);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", "bill", "monthly", (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidDayOfMonthErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_InvalidDayAbove31() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(32);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", "bill", "monthly", (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidDayOfMonthErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_NullExpirationDt() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", "bill", "monthly", (float) 50, days, null, null));
		});

		assert (e.getMessage().equals(myConfig.getEffectiveDateRequiredErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_NullCategory() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", null, "monthly", (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidCategoryErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_BlankCategory() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", "", "monthly", (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidCategoryErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_InvalidCategory() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", "iaminvalid", "monthly", (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidCategoryErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_NullFrequency() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", "bill", null, (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidFrequencyErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_EmptyFrequency() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", "bill", "", (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidFrequencyErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_InvalidFrequency() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"income", "bill", "iaminvalid", (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidFrequencyErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_NullType() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction", null,
					"bill", "monthly", (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidTransactionTypeErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_EmptyType() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction", "",
					"bill", "monthly", (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidTransactionTypeErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_InvalidType() {
		FinanceManagerException e;

		List<Integer> days = new ArrayList<>();
		days.add(1);

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction",
					"iaminvalid", "bill", "monthly", (float) 50, days, new Date(), null));
		});

		assert (e.getMessage().equals(myConfig.getInvalidTransactionTypeErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

	}

	@Test
	public void testValidateTransactionPostRequest_Valid() {

		List<Integer> days = new ArrayList<>();
		days.add(1);

		transactionValidation.validateTransactionPostRequest(1, new TransactionPostRequest("testTransaction", "income",
				"bill", "monthly", (float) 50, days, new Date(), null));

	}

	@Test
	public void testValidateGetAllTransactionParameters_Valid() {

		transactionValidation.validateGetAllTransactionParameters(1, null, null, null, null, null, null, null);
		transactionValidation.validateGetAllTransactionParameters(1, "", "", "", "", "", null, null);
		transactionValidation.validateGetAllTransactionParameters(1, "incomes", "bill", "income", "name", "asc", 1, 1);

	}

	@Test
	public void testValidateGetAllTransactionParameters_NullUserId() {
		FinanceManagerException e;

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateGetAllTransactionParameters(null, null, null, null, null, null, null, null);
		});

		assert (e.getMessage().equals(myConfig.getUserIdRequiredError()));
		assertEquals(400, e.getStatusCode().value());
	}

	@Test
	public void testValidateGetAllTransactionParameters_InvalidUserId() {
		FinanceManagerException e;

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateGetAllTransactionParameters(0, null, null, null, null, null, null, null);
		});

		assert (e.getMessage().equals(myConfig.getUserIdRequiredError()));
		assertEquals(400, e.getStatusCode().value());
	}
	
	@Test
	public void testValidateGetAllTransactionParameters_InvalidParentTableName() {
		FinanceManagerException e;

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateGetAllTransactionParameters(1, "iaminvalid", null, null, null, null, null, null);
		});

		assert (e.getMessage().equals(myConfig.getInvalidTransactionSearchParentName()));
		assertEquals(400, e.getStatusCode().value());
	}

	@Test
	public void testValidateGetAllTransactionParameters_InvalidCategory() {
		FinanceManagerException e;

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateGetAllTransactionParameters(1, null, "iaminvalid", null, null, null, null, null);
		});

		assert (e.getMessage().equals(myConfig.getInvalidTransactionSearchCategory()));
		assertEquals(400, e.getStatusCode().value());
	}

	@Test
	public void testValidateGetAllTransactionParameters_InvalidType() {
		FinanceManagerException e;

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateGetAllTransactionParameters(1, null, null, "iaminvalid", null, null, null, null);
		});

		assert (e.getMessage().equals(myConfig.getInvalidTransactionSearchType()));
		assertEquals(400, e.getStatusCode().value());
	}

	@Test
	public void testValidateGetAllTransactionParameters_InvalidSortBy() {
		FinanceManagerException e;

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateGetAllTransactionParameters(1, null, null, null, "iaminvalid", null, null, null);
		});

		assert (e.getMessage().equals(myConfig.getInvalidSortByErrorMsg()));
		assertEquals(400, e.getStatusCode().value());
	}

	@Test
	public void testValidateGetAllTransactionParameters_InvalidSortType() {
		FinanceManagerException e;

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateGetAllTransactionParameters(1, null, null, null, null, "iaminvalid", null, null);
		});

		assert (e.getMessage().equals(myConfig.getInvalidSortTypeErrorMsg()));
		assertEquals(400, e.getStatusCode().value());
	}

	@Test
	public void testValidateGetAllTransactionParameters_InvalidPagination() {
		FinanceManagerException e;

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateGetAllTransactionParameters(1, null, null, null, null, null, 0, null);
		});

		assert (e.getMessage().equals(myConfig.getInvalidPagingParameterErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateGetAllTransactionParameters(1, null, null, null, null, null, 0, 0);
		});

		assert (e.getMessage().equals(myConfig.getInvalidPagingParameterErrorMsg()));
		assertEquals(400, e.getStatusCode().value());

		e = assertThrows(FinanceManagerException.class, () -> {
			transactionValidation.validateGetAllTransactionParameters(1, null, null, null, null, null, null, 0);
		});

		assert (e.getMessage().equals(myConfig.getInvalidPagingParameterErrorMsg()));
		assertEquals(400, e.getStatusCode().value());
	}

}
