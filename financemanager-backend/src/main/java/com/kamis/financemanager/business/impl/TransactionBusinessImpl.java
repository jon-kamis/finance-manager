package com.kamis.financemanager.business.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.business.TransactionBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.database.domain.Transaction;
import com.kamis.financemanager.database.domain.TransactionDay;
import com.kamis.financemanager.database.repository.TransactionRepository;
import com.kamis.financemanager.database.specifications.GenericSpecification;
import com.kamis.financemanager.database.specifications.QueryOperation;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.enums.TableNameEnum;
import com.kamis.financemanager.enums.TransactionCategoryEnum;
import com.kamis.financemanager.enums.TransactionTypeEnum;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.factory.TransactionFactory;
import com.kamis.financemanager.rest.domain.transactions.PagedTransactionResponse;
import com.kamis.financemanager.rest.domain.transactions.TransactionPostRequest;
import com.kamis.financemanager.validation.TransactionValidation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TransactionBusinessImpl implements TransactionBusiness {

	@Autowired
	private YAMLConfig myConfig;

	@Autowired
	private TransactionValidation transactionValidation;

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public boolean createTransaction(Integer userId, TransactionPostRequest request) throws FinanceManagerException {

		transactionValidation.validateTransactionPostRequest(userId, request);
        transactionRepository.save(TransactionFactory.buildTransactionFromPostRequest(userId, request));

		return true;
	}

	@Override
	@Transactional
	public PagedTransactionResponse getAllUserTransactions(Integer userId, String name, String parentName, String category, String type,
			String sortBy, String sortType, Integer page, Integer pageSize) throws FinanceManagerException {

		transactionValidation.validateGetAllTransactionParameters(userId, parentName, category, type, sortBy, sortType, page,
				pageSize);

		GenericSpecification<Transaction> spec = new GenericSpecification<Transaction>().where("userId", userId,
				QueryOperation.EQUALS);

		if (name != null && !name.isBlank()) {
			spec.and("name", name, QueryOperation.CONTAINS);
		}
		
		if (parentName != null && !parentName.isBlank()) {
			spec.and("parentTableName", TableNameEnum.valueOfLabel(parentName), QueryOperation.EQUALS);
		}

		if (category != null && !category.isBlank()) {
			spec.and("category", TransactionCategoryEnum.valueOfLabel(category), QueryOperation.EQUALS_OBJECT);
		}

		if (type != null && !type.isBlank()) {
			spec.and("type", TransactionTypeEnum.valueOfLabel(type), QueryOperation.EQUALS_OBJECT);
		}

		if (page == null || page < 1) {
			page = 1;
		}

		// Set sort direction
		boolean sortAsc = sortType == null || sortType.isBlank()
				|| sortType.equalsIgnoreCase(FinanceManagerConstants.SORT_TYPE_ASC);

		// Handle sorting
		List<Order> orders = new ArrayList<>();

		if (sortBy != null && !sortBy.isBlank() && !sortBy.equals(FinanceManagerConstants.TRANSACTION_SORT_BY_NAME)) {
			orders.add(new Order(sortAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
			orders.add(new Order(sortAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
					FinanceManagerConstants.TRANSACTION_SORT_BY_NAME));
		} else {
			orders.add(new Order(sortAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
					FinanceManagerConstants.TRANSACTION_SORT_BY_NAME));
			orders.add(new Order(sortAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
					FinanceManagerConstants.TRANSACTION_SORT_BY_AMOUNT));
		}

		Sort sort = Sort.by(orders);

		// Set pagination
		Pageable pageable = null;
		boolean doCountQuery = false;

		if (pageSize != null && pageSize >= 1) {
			pageable = PageRequest.of(page - 1, pageSize, sort);
			doCountQuery = true;
		}

		List<Transaction> transactions;

		if (pageable != null) {
			transactions = transactionRepository.findAll(spec.build(), pageable).toList();
		} else {
			transactions = transactionRepository.findAll(spec.build(), sort);
		}

		int count = doCountQuery ? (int)transactionRepository.count(spec.build()) : transactions.size();

		if (pageSize == null) {
			pageSize = count;
		}

		return TransactionFactory.buildPagedTransactionResponse(transactions, page, pageSize, count);
	}

	@Override
	@Transactional
	public List<Date> getPaysInDateRange(Transaction t, Date startDate, Date endDate) throws FinanceManagerException {
		LocalDate localStart = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate localEnd = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		if (startDate.after(endDate)) {
			log.info(
					"failed to get pays in date range for transaction due to invalid start or end date. startDate: {}, endDate: {}",
					startDate, endDate);
			throw new FinanceManagerException("Invalid start or end date to get transaction occurrences",
					HttpStatus.UNPROCESSABLE_ENTITY);
		}

		if (t.getEffectiveDate().after(endDate) || (t.getExpirationDate() != null && t.getExpirationDate().before(startDate))) {
			return new ArrayList<>();
		}

        if (Objects.requireNonNull(t.getFrequency()) == PaymentFrequencyEnum.SEMI_MONTHLY) {// Verify transaction Days
            if (t.getTransactionDays() == null || t.getTransactionDays().isEmpty()) {
                log.error("expected exactly one transaction day entry for transaction frequency but found none");
                return new ArrayList<>();
            }

            return getSemiMonthlyTransactionOccurrences(localStart, localEnd, t);
        } else {
        if (t.getTransactionDays() == null || t.getTransactionDays().isEmpty()) {
            log.error("expected exactly one transaction day entry for transaction frequency but found none");
            return new ArrayList<>();

        } else if (t.getTransactionDays().size() > 1) {
            log.error("expected exactly one transaction day entry for transaction frequency but found {}",
                    t.getTransactionDays().size());
            return new ArrayList<>();
        }
}
        return getNonSemiMonthlyTransactionOccurrences(localStart, localEnd, t);
    }

	/**
	 * Generates a list of transaction occurrences for semi-monthly pay frequencies
	 * 
	 * @param startDate The starting Date of the range to generate occurrences for
	 * @param endDate   The ending Date of the range to generate occurrences for
	 * @param t         The transaction to generate occurrences for
	 * @return A List of dates transactions will occur for the given criteria
	 */
	private List<Date> getSemiMonthlyTransactionOccurrences(LocalDate startDate, LocalDate endDate, Transaction t) {
		List<Date> occurrences = new ArrayList<>();
		LocalDate curMonth = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
		LocalDate expirationDate = null;
		LocalDate effectiveDate = t.getEffectiveDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate occDate;

		if (t.getExpirationDate() != null) {
			expirationDate = t.getExpirationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}

		while (!adjustTransactionOccurrence(curMonth).isAfter(endDate)
				&& (expirationDate == null || !curMonth.isAfter(expirationDate))) {

			for (TransactionDay day : t.getTransactionDays()) {

				// Handle semi-monthly pay falling after end of month
				if (curMonth.getMonth().length(curMonth.isLeapYear()) < day.getDay()) {
					occDate = adjustTransactionOccurrence(LocalDate.of(curMonth.getYear(), curMonth.getMonth(),
							curMonth.getMonth().length(curMonth.isLeapYear())));
				} else {
					occDate = adjustTransactionOccurrence(
							LocalDate.of(curMonth.getYear(), curMonth.getMonth(), day.getDay()));
				}

				if (!occDate.isBefore(startDate) && !occDate.isBefore(effectiveDate)
						&& !occDate.isAfter(endDate)
						&& (expirationDate == null || !occDate.isAfter(expirationDate))) {
					
					occurrences.add(Date.from(
							adjustTransactionOccurrence(occDate).atStartOfDay(ZoneId.systemDefault()).toInstant()));
				}
			}

			curMonth = curMonth.plusMonths(1);
		}

		return occurrences;
	}

	/**
	 * Generates a list of transaction occurrences for all pay types other than
	 * semi-monthly
	 * 
	 * @param startDate The starting Date of the range to generate occurrences for
	 * @param endDate   The ending Date of the range to generate occurrences for
	 * @param t         The transaction to generate occurrences for
	 * @return A List of dates transactions will occur for the given criteria
	 */
	private List<Date> getNonSemiMonthlyTransactionOccurrences(LocalDate startDate, LocalDate endDate, Transaction t) {
		List<Date> occurrences = new ArrayList<>();
		LocalDate curOccurrence;
		LocalDate expirationDate = null;
		LocalDate effectiveDate = t.getEffectiveDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		if (t.getFrequency() != PaymentFrequencyEnum.WEEKLY) {
			curOccurrence = t.getTransactionDays().getFirst().getStartDate().toInstant().atZone(ZoneId.systemDefault())
					.toLocalDate();
		} else {
			curOccurrence = startDate;

			while (curOccurrence.getDayOfWeek().getValue() < t.getTransactionDays().getFirst().getWeekday().getDayIndex()) {
				curOccurrence = curOccurrence.plusDays(1);
			}
		}

		if (t.getExpirationDate() != null) {
			expirationDate = t.getExpirationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}

		while (!adjustTransactionOccurrence(curOccurrence).isAfter(endDate)
				&& (expirationDate == null || !curOccurrence.isAfter(expirationDate))) {

			LocalDate adjustedOccurrence = adjustTransactionOccurrence(curOccurrence);

			if (!adjustedOccurrence.isBefore(startDate) && !adjustedOccurrence.isBefore(effectiveDate)) {
				occurrences.add(Date.from(
						adjustTransactionOccurrence(curOccurrence).atStartOfDay(ZoneId.systemDefault()).toInstant()));
			}

            curOccurrence = switch (t.getFrequency()) {
                case ANNUAL -> curOccurrence.plusYears(1);
                case BIWEEKLY -> curOccurrence.plusWeeks(2);
                case MONTHLY -> curOccurrence.plusMonths(1);
                case QUARTERLY -> curOccurrence.plusMonths(3);
                case WEEKLY -> curOccurrence.plusWeeks(1);
                default -> {
                    log.error("Unexpected frequency for transaction");
                    throw new FinanceManagerException(myConfig.getGenericInternalServerErrorMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            };
		}

		return occurrences;
	}

	/**
	 * Adjusts an occurrence date if it falls on a weekend
	 * 
	 * @param occ The occurrence date to adjust
	 * @return A LocalDate adjusted if it fell on the weekend or unchanged if during
	 *         a weekday
	 */
	private LocalDate adjustTransactionOccurrence(LocalDate occ) {

		// If the occurrence is on a Saturday or the last Sunday of a month it will
		// instead occur on the previous Friday
		// If the occurrence is on a Sunday, and it is not the last Sunday of the month
		// it will instead occur on the following Monday
		if (occ.getDayOfWeek() == DayOfWeek.SATURDAY || occ.plusDays(1).getMonthValue() > occ.getMonthValue()) {

			return occ.minusDays(1);

		} else if (occ.getDayOfWeek() == DayOfWeek.SUNDAY) {

			return occ.plusDays(1);
		}

		return occ;
	}
}
