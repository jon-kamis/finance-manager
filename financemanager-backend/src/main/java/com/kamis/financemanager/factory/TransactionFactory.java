package com.kamis.financemanager.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.kamis.financemanager.database.domain.Income;
import com.kamis.financemanager.database.domain.Transaction;
import com.kamis.financemanager.database.domain.TransactionDay;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.enums.TableNameEnum;
import com.kamis.financemanager.enums.TransactionCategoryEnum;
import com.kamis.financemanager.enums.TransactionTypeEnum;
import com.kamis.financemanager.enums.WeekdayEnum;
import com.kamis.financemanager.rest.domain.incomes.IncomePostRequest;
import com.kamis.financemanager.rest.domain.transactions.PagedTransactionResponse;
import com.kamis.financemanager.rest.domain.transactions.TransactionDayResponse;
import com.kamis.financemanager.rest.domain.transactions.TransactionPostRequest;
import com.kamis.financemanager.rest.domain.transactions.TransactionResponse;
import com.kamis.financemanager.util.FinanceManagerUtil;

public class TransactionFactory {

	/**
	 * Builds a new Transaction object based on a post request
	 * 
	 * @param userId  The id of the user to build the request for
	 * @param request The request containing information on how to build the new
	 *                transaction
	 * @return A Transaction object built from the given request
	 */
	public static Transaction buildTransactionFromPostRequest(Integer userId, TransactionPostRequest request) {
		Transaction transaction = new Transaction();

		transaction.setName(request.getName());
		transaction.setUserId(userId);
		transaction.setAmount(request.getAmount());
		transaction.setCategory(TransactionCategoryEnum.valueOfLabel(request.getCategory()));
		transaction.setFrequency(PaymentFrequencyEnum.valueOfLabel(request.getFrequency()));
		transaction.setType(TransactionTypeEnum.valueOfLabel(request.getType()));
		transaction.setEffectiveDate(request.getEffectiveDate());
		transaction.setExpirationDate(request.getExpirationDate());
		transaction.setAuditInfo(FinanceManagerUtil.getAuditInfo());

		if (request.getDaysOfMonth() != null && !request.getDaysOfMonth().isEmpty()) {
			for (int day : request.getDaysOfMonth()) {
				transaction.addTransactionDay(buildTransactionDay(day));
			}
		}

		return transaction;
	}

	/**
	 * Builds a new TransactionDay object
	 * 
	 * @param day The day of the transaction
	 * @return A TransactionDay object
	 */
	public static TransactionDay buildTransactionDay(int day) {
		TransactionDay transactionDay = new TransactionDay();
		transactionDay.setDay(day);
		transactionDay.setAuditInfo(FinanceManagerUtil.getAuditInfo());
		return transactionDay;
	}

	/**
	 * Builds a PagedTransactionResponse based on a list of transactions and
	 * pagination details
	 * 
	 * @param transactions The transactions to build responses for
	 * @param page         The requested page
	 * @param pageSize     The requested pageSize
	 * @param count        The total number of results
	 * @return A PagedTransactionResponse
	 */
	public static PagedTransactionResponse buildPagedTransactionResponse(List<Transaction> transactions, int page,
			int pageSize, int count) {
		PagedTransactionResponse response = new PagedTransactionResponse();

		List<TransactionResponse> items = new ArrayList<>();

		items.addAll(transactions.stream().map(t -> buildTransactionResponse(t)).collect(Collectors.toList()));
		response.setCount(count);
		response.setPage(page);
		response.setPageSize(pageSize);
		response.setItems(items);

		return response;
	}

	/**
	 * Builds a TransactionResponse based on a transaction
	 * 
	 * @param transaction The transaction to build a response for
	 * @return A TransactionResponse
	 */
	public static TransactionResponse buildTransactionResponse(Transaction transaction) {
		TransactionResponse response = new TransactionResponse();
		response.setId(transaction.getId());
		response.setName(transaction.getName());
		response.setAmount(transaction.getAmount());
		response.setCategory(transaction.getCategory().getCategory());
		response.setFrequency(transaction.getFrequency().getFrequency());
		response.setType(transaction.getType().getType());

		response.setEffectiveDate(transaction.getEffectiveDate());
		response.setExpirationDate(transaction.getExpirationDate());

		response.setTransactionDays(transaction.getTransactionDays().stream().map(td -> buildTransactionDayResponse(td)).collect(Collectors.toList()));

		return response;
	}

	public static TransactionDayResponse buildTransactionDayResponse(TransactionDay td) {
		TransactionDayResponse resp = new TransactionDayResponse();
		
		resp.setWeekday(td.getWeekday() != null ? td.getWeekday().getWeekday() : null);
		resp.setStartDate(td.getStartDate() != null ? td.getStartDate() : null);
		resp.setDay(td.getDay());
		
		return resp;
	}

	/**
	 * Builds a new transaction object for an income object
	 * 
	 * @param userId  The income that will own this transaction
	 * @param request The income post request
	 * @return A transaction object built for the given income
	 */
	public static Transaction buildTransactionFromIncomePostRequest(Income income, IncomePostRequest request) {
		Transaction transaction = new Transaction();
		PaymentFrequencyEnum frequency = PaymentFrequencyEnum.valueOfLabel(request.getFrequency());

		transaction.setAmount(request.getAmount());
		transaction.setCategory(TransactionCategoryEnum.valueOfLabel(request.getCategory()));
		transaction.setEffectiveDate(request.getEffectiveDate());
		transaction.setExpirationDate(request.getExpirationDate());
		transaction.setFrequency(frequency);
		transaction.setName(request.getName());
		transaction.setUserId(income.getUserId());
		transaction.setType(TransactionTypeEnum.INCOME);
		transaction.setParentId(income.getId());
		transaction.setParentTableName(TableNameEnum.INCOMES);
		transaction.setAuditInfo(FinanceManagerUtil.getAuditInfo());

		switch (frequency) {
		case BIWEEKLY:
			
			if (request.getStartDate() != null) {
				transaction.addTransactionDay(buildTransactionDayBiweekly(request.getStartDate()));
			}
			
			break;
		case MONTHLY:
		case SEMI_MONTHLY:
			
			if (request.getDaysOfMonth() != null && !request.getDaysOfMonth().isEmpty()) {
				for (int day : request.getDaysOfMonth()) {
					transaction.addTransactionDay(buildTransactionDay(day));
				}
			}

			break;
		case WEEKLY:
			
			if (request.getWeekday() != null && !request.getWeekday().isBlank()) {
				transaction.addTransactionDay(buildTransactionDayWeekly(request.getWeekday()));
			}
			
			break;
		default:
			break;
		}

		return transaction;
	}

	//Builds a TransactionDay for a Weekly pay frequency
	private static TransactionDay buildTransactionDayWeekly(String weekday) {
		TransactionDay day = new TransactionDay();
		day.setWeekday(WeekdayEnum.valueOfLabel(weekday));
		day.setAuditInfo(FinanceManagerUtil.getAuditInfo());
		return day;
	}

	//Builds a transactionDay for a biweekly pay frequency
	private static TransactionDay buildTransactionDayBiweekly(Date startDate) {
		TransactionDay day = new TransactionDay();
		day.setStartDate(startDate);
		day.setAuditInfo(FinanceManagerUtil.getAuditInfo());
		return day;
	}
}
