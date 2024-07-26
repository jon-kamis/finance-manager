package com.kamis.financemanager.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.business.TransactionBusiness;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.database.domain.Transaction;
import com.kamis.financemanager.database.repository.TransactionRepository;
import com.kamis.financemanager.database.specifications.GenericSpecification;
import com.kamis.financemanager.database.specifications.QueryOperation;
import com.kamis.financemanager.enums.TransactionCategoryEnum;
import com.kamis.financemanager.enums.TransactionTypeEnum;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.factory.TransactionFactory;
import com.kamis.financemanager.rest.domain.transactions.PagedTransactionResponse;
import com.kamis.financemanager.rest.domain.transactions.TransactionPostRequest;
import com.kamis.financemanager.validation.TransactionValidation;

@Component
public class TransactionBusinessImpl implements TransactionBusiness {

	@Autowired
	private TransactionValidation transactionValidation;

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public boolean createTransaction(Integer userId, TransactionPostRequest request) throws FinanceManagerException {

		transactionValidation.validateTransactionPostRequest(userId, request);

		return transactionRepository.save(TransactionFactory.buildTransactionFromPostRequest(userId, request)) != null;
	}

	@Override
	public PagedTransactionResponse getAllUserTransactions(Integer userId, String name, String category, String type,
			String sortBy, String sortType, Integer page, Integer pageSize) throws FinanceManagerException {

		transactionValidation.validateGetAllTransactionParameters(userId, category, type, sortBy, sortType, page,
				pageSize);

		GenericSpecification<Transaction> spec = new GenericSpecification<Transaction>().where("userId", userId,
				QueryOperation.EQUAL);

		if (name != null && !name.isBlank()) {
			spec.and("name", name, QueryOperation.CONTAINS);
		}

		if (category != null && !category.isBlank()) {
			spec.and("category", TransactionCategoryEnum.valueOfLabel(category), QueryOperation.EQUAL);
		}

		if (type != null && !type.isBlank()) {
			spec.and("type", TransactionTypeEnum.valueOfLabel(type), QueryOperation.EQUAL);
		}

		if (page == null || page < 1) {
			page = 1;
		}

		// Set sort direction
		boolean sortAsc = sortType == null || sortType.isBlank()
				|| sortType.equalsIgnoreCase(FinanceManagerConstants.SORT_TYPE_ASC);

		// Handle sorting
		List<Order> orders = new ArrayList<Order>();

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
			pageable = PageRequest.of(page-1, pageSize, sort);
			doCountQuery = true;
		}

		List<Transaction> transactions;

		if (pageable != null) {
			transactions = transactionRepository.findAll(spec.build(), pageable);
		} else {
			transactions = transactionRepository.findAll(spec.build(), sort);
		}
		
		int count = doCountQuery ? transactionRepository.count(spec.build()) : transactions.size();

		if (pageSize == null) {
			pageSize = count;
		}

		return TransactionFactory.buildPagedTransactionResponse(transactions, page, pageSize, count);
	}
}
