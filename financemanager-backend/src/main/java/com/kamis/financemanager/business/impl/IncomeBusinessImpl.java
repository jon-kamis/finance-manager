package com.kamis.financemanager.business.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.business.IncomeBusiness;
import com.kamis.financemanager.business.TransactionBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.database.domain.Income;
import com.kamis.financemanager.database.domain.StandardWithholding;
import com.kamis.financemanager.database.domain.Transaction;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.IncomeRepository;
import com.kamis.financemanager.database.repository.StandardWithholdingRepository;
import com.kamis.financemanager.database.repository.TransactionRepository;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.database.specifications.GenericSpecification;
import com.kamis.financemanager.database.specifications.QueryOperation;
import com.kamis.financemanager.enums.FilingTypeEnum;
import com.kamis.financemanager.enums.TableNameEnum;
import com.kamis.financemanager.enums.TransactionCategoryEnum;
import com.kamis.financemanager.enums.TransactionTypeEnum;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.factory.IncomeFactory;
import com.kamis.financemanager.factory.TransactionFactory;
import com.kamis.financemanager.rest.domain.incomes.IncomeMonth;
import com.kamis.financemanager.rest.domain.incomes.IncomePostRequest;
import com.kamis.financemanager.rest.domain.incomes.IncomeResponse;
import com.kamis.financemanager.rest.domain.incomes.IncomeSummary;
import com.kamis.financemanager.rest.domain.incomes.IncomeSummaryResponse;
import com.kamis.financemanager.rest.domain.incomes.PagedIncomeResponse;
import com.kamis.financemanager.util.FinanceManagerUtil;
import com.kamis.financemanager.validation.IncomeValidation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IncomeBusinessImpl implements IncomeBusiness {

	@Autowired
	private YAMLConfig myConfig;

	@Autowired
	private IncomeValidation incomeValidation;

	@Autowired
	private IncomeRepository incomeRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TransactionBusiness transactionBusiness;

	@Autowired
	private StandardWithholdingRepository standardWithholdingRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	@Transactional
	public boolean createIncome(Integer userId, IncomePostRequest request) throws FinanceManagerException {

		// Fetch user from the db
		Optional<User> user = userRepository.findById(userId);

		if (user.isEmpty()) {
			log.error("user with id {} should exist to have reached this point, but it could not be found", userId);
			throw new FinanceManagerException(myConfig.getGenericNotFoundErrorMsg(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// First save new income
		Income income = incomeRepository.save(IncomeFactory.createIncome(user.get(), request));

		if (income.getId() == null) {
			log.error("attempted to fetch income id but value was null");
			throw new FinanceManagerException(myConfig.getGenericInternalServerErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<Transaction> transactions = new ArrayList<>();
		Transaction paycheck = TransactionFactory.buildTransactionFromIncomePostRequest(income, request);

		if (paycheck.getTransactionDays() == null || paycheck.getTransactionDays().isEmpty()) {
			log.error("expected request to have generated at least one transaction day but none were present");
			throw new FinanceManagerException(myConfig.getUnableToBuildTransactionDaysErrorMsg(),
					HttpStatus.UNPROCESSABLE_ENTITY);
		}

		transactions.add(paycheck);

		if (request.getTaxable().booleanValue()) {

			Transaction fedtax = TransactionFactory.buildTransactionFromIncomePostRequest(income, request);
			fedtax.setAmount(calculateStandardFederalTaxes(income));
			fedtax.setName(paycheck.getName() + " " + FinanceManagerConstants.TAX_NAME_FEDERAL);
			fedtax.setType(TransactionTypeEnum.EXPENSE);
			fedtax.setCategory(TransactionCategoryEnum.TAXES);
			transactions.add(fedtax);

			Transaction socSecTax = TransactionFactory.buildTransactionFromIncomePostRequest(income, request);
			socSecTax.setAmount(calculateSocialSecurityTax(income));
			socSecTax.setName(paycheck.getName() + " " + FinanceManagerConstants.TAX_NAME_SOC_SEC);
			socSecTax.setType(TransactionTypeEnum.EXPENSE);
			socSecTax.setCategory(TransactionCategoryEnum.TAXES);
			transactions.add(socSecTax);

			Transaction medicare = TransactionFactory.buildTransactionFromIncomePostRequest(income, request);
			medicare.setAmount(calculateMedicareTax(income));
			medicare.setName(paycheck.getName() + " " + FinanceManagerConstants.TAX_NAME_MEDICARE);
			medicare.setType(TransactionTypeEnum.EXPENSE);
			medicare.setCategory(TransactionCategoryEnum.TAXES);
			transactions.add(medicare);

			if (income.getUser().getState().isTaxEnabled()) {
				switch (income.getUser().getState()) {
				case PENNSYLVANIA:
					transactions.addAll(buildTransactionsForPaTax(income, request));
					break;
				default:
					log.error("state income tax is enabled for state {} but no calculation exists",
							income.getUser().getState().getState());
					throw new FinanceManagerException(myConfig.getStateTaxCalcMissingErrorMsg(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}

		// Perform save
		transactionRepository.saveAll(transactions);
		return true;
	}

	public List<Transaction> buildTransactionsForPaTax(Income income, IncomePostRequest request) {

		List<Transaction> stateTaxes = new ArrayList<>();

		Transaction stateIncome = TransactionFactory.buildTransactionFromIncomePostRequest(income, request);
		stateIncome.setAmount(income.getAmount() * myConfig.getPaIncomeTaxRate());
		stateIncome.setName(FinanceManagerConstants.TAX_NAME_PA_STATE);
		stateIncome.setType(TransactionTypeEnum.EXPENSE);
		stateIncome.setCategory(TransactionCategoryEnum.TAXES);
		stateTaxes.add(stateIncome);

		Transaction unemp = TransactionFactory.buildTransactionFromIncomePostRequest(income, request);
		unemp.setAmount(income.getAmount() * myConfig.getPaUnemploymentTaxRate());
		unemp.setName(FinanceManagerConstants.TAX_NAME_PA_UNEMP);
		unemp.setType(TransactionTypeEnum.EXPENSE);
		unemp.setCategory(TransactionCategoryEnum.TAXES);
		stateTaxes.add(unemp);

		if (income.getUser().getLocalTaxRate() != null && income.getUser().getLocalTaxRate() > 0) {

			Transaction local = TransactionFactory.buildTransactionFromIncomePostRequest(income, request);
			local.setAmount(income.getAmount() * income.getUser().getLocalTaxRate());
			local.setName(FinanceManagerConstants.TAX_NAME_LOCAL);
			local.setType(TransactionTypeEnum.EXPENSE);
			local.setCategory(TransactionCategoryEnum.TAXES);
			stateTaxes.add(local);
		}

		return stateTaxes;
	}

	@Override
	public IncomeResponse findByUserIdAndId(Integer userId, Integer id) throws FinanceManagerException {

		GenericSpecification<Income> incomeSpec = new GenericSpecification<>();
		incomeSpec = incomeSpec.where("id", id, QueryOperation.EQUALS).and("userId", userId, QueryOperation.EQUALS);

		Optional<Income> income = incomeRepository.findOne(incomeSpec.build());

		if (income.isEmpty()) {
			log.info("attempted to find income with id {} for user with id {} but none was found", id, userId);
			return null;
		}

		GenericSpecification<Transaction> transactionSpec = new GenericSpecification<>();
		transactionSpec = transactionSpec.where("parentId", income.get().getId(), QueryOperation.EQUALS)
				.and("parentTableName", TableNameEnum.INCOMES, QueryOperation.EQUALS_OBJECT);

		List<Transaction> transactions = transactionRepository.findAll(transactionSpec.build());

		return IncomeFactory.buildIncomeResponse(income.get(), transactions);
	}

	@Override
	public float calculateStandardFederalTaxes(Income income) throws FinanceManagerException {

		if (income.getFilingType() == null) {
			throw new FinanceManagerException(myConfig.getFilingTypeRequiredForTaxesError(),
					HttpStatus.UNPROCESSABLE_ENTITY);
		}

		float grossIncome = income.getAmount() * income.getFrequency().getNumPays();
		float tax = 0;
		float credits = 0;
		float extraWithholding = 0;

		// Adjust annual payment amount
		if (income.getFilingType() == FilingTypeEnum.MARRIED_FILING_JOINTLY) {
			grossIncome -= 12900;
		} else {
			grossIncome -= 8600;
		}

		if (income.getWithheldTax() != null && income.getWithheldTax() > 0) {
			extraWithholding = income.getWithheldTax() / (float) income.getFrequency().getNumPays();
		}

		if (income.getTaxCredits() != null && income.getTaxCredits() > 0) {
			credits += (income.getTaxCredits() * 2000) / (float) income.getFrequency().getNumPays();
		}

		StandardWithholding withholding = getStandardWithholding(grossIncome, income.getFilingType());

		tax = (((grossIncome - withholding.getMin()) * (withholding.getPercentage() / 100))
				+ withholding.getBaseAmount()) / income.getFrequency().getNumPays();

		tax = Math.max((tax - credits), 0) + extraWithholding;

		return tax;
	}

	@Override
	public float calculateSocialSecurityTax(Income income) {

		// TODO: Add functionality to cut off social security if annual limit of
		// 10543.20 is reached
		return income.getAmount() * (float) 0.062;
	}

	@Override
	public float calculateMedicareTax(Income income) {
		return income.getAmount() * (float) 0.0145;
	}

	/**
	 * Gets the Standard withholding information for an income
	 * 
	 * @param grossIncome The grossIncome to query by
	 * @return A StandardWithholding object
	 * @throws FinanceManagerException
	 */
	private StandardWithholding getStandardWithholding(float grossIncome, FilingTypeEnum filingType)
			throws FinanceManagerException {

		GenericSpecification<StandardWithholding> filingTypeSpec = new GenericSpecification<>();
		GenericSpecification<StandardWithholding> betweenSpec = new GenericSpecification<>();
		GenericSpecification<StandardWithholding> maxSpec = new GenericSpecification<>();

		filingTypeSpec = filingTypeSpec.where("filingType", filingType, QueryOperation.EQUALS_OBJECT);

		betweenSpec = betweenSpec.where("min", grossIncome, QueryOperation.LESS_THAN_EQUAL_TO).and("max", grossIncome,
				QueryOperation.GREATER_THAN);

		maxSpec = maxSpec.where("min", grossIncome, QueryOperation.LESS_THAN_EQUAL_TO).and("max", grossIncome,
				QueryOperation.IS_NULL);

		Optional<StandardWithholding> withholding = standardWithholdingRepository
				.findOne(Specification.where(filingTypeSpec.build()).and(betweenSpec.build().or(maxSpec.build())));

		if (withholding.isEmpty()) {
			log.error("failed to find standard withholding for filing type {} and gross income of {}",
					filingType.getFilingType(), grossIncome);
			throw new FinanceManagerException(myConfig.getUnableToFindStandardWithholdingErrorMsg(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return withholding.get();
	}

	@Override
	public PagedIncomeResponse getUserIncomes(Integer userId, String name, String sortBy, String sortType, Integer page,
			Integer pageSize) throws FinanceManagerException {

		// Validate request
		incomeValidation.validateGetAllUserIncomesRequest(userId, sortBy, sortType, page, pageSize);

		List<Income> incomes;
		int count;

		/* Build query specifications */

		GenericSpecification<Income> spec = new GenericSpecification<>();
		spec = spec.where("userId", userId, QueryOperation.EQUALS);

		if (name != null && !name.isBlank()) {
			spec = spec.and("name", name, QueryOperation.CONTAINS);
		}

		/* Build Sorting options */

		String sortByCleaned = FinanceManagerConstants.LOAN_VALID_SORT_TYPES.stream()
				.filter(s -> s.equalsIgnoreCase(sortBy)).findFirst().orElse(null);

		Sort sort = FinanceManagerUtil.buildSort(sortByCleaned, sortType);
		Pageable pageable = FinanceManagerUtil.buildPageable(page, pageSize, sort);

		if (pageable != null) {
			incomes = incomeRepository.findAll(spec.build(), pageable).toList();
			count = (int) incomeRepository.count(spec.build());
		} else if (sort != null) {
			incomes = incomeRepository.findAll(spec.build(), sort);
			count = incomes.size();
		} else {
			incomes = incomeRepository.findAll(spec.build());
			count = incomes.size();
		}

		// Generate a mapping of each income to its list of transactions
		List<Transaction> allTransactions = transactionRepository.findByUserId(userId);
		Map<Integer, List<Transaction>> transactionMap = new HashMap<>();

		for (Income i : incomes) {
			transactionMap.put(i.getId(),
					allTransactions.stream().filter(
							t -> t.getParentId() == i.getId() && t.getParentTableName().equals(TableNameEnum.INCOMES))
							.toList());
		}

		return IncomeFactory.buildPagedIncomeResponse(incomes, transactionMap, page, pageSize, count);
	}

	@Override
	public IncomeSummaryResponse getUserIncomeSummary(Integer userId, Date date) throws FinanceManagerException {

		IncomeSummaryResponse response = new IncomeSummaryResponse();
		IncomeSummary monthSummary = new IncomeSummary();
		IncomeSummary annualSummary = new IncomeSummary();
		Map<Integer, IncomeMonth> earningsMap = new HashMap<>();
		List<IncomeMonth> earningsForcast = new ArrayList<>();

		if (date == null) {
			date = new Date();
		}

		Date yearStart = FinanceManagerUtil.getStartOfYear(date);
		Date yearEnd = FinanceManagerUtil.getEndOfYear(date);
		Date monthStart = FinanceManagerUtil.getStartOfMonth(date);
		Date monthEnd = FinanceManagerUtil.getEndOfMonth(date);

		/* Fetch all incomes */
		GenericSpecification<Income> spec = new GenericSpecification<>();
		spec = spec.where("userId", userId, QueryOperation.EQUALS);
		List<Income> incomes = incomeRepository.findAll(spec.build());

		/* Fetch all transactions for the found incomes */
		GenericSpecification<Transaction> tSpec = new GenericSpecification<>();
		tSpec = tSpec.where("userId", userId, QueryOperation.EQUALS).and("parentId",
				incomes.stream().map(i -> i.getId()).toList(), QueryOperation.IN);

		List<Transaction> allTransactions = transactionRepository.findAll(tSpec.build());
		List<Date> yearDates;
		List<Date> monthDates;

		float monthIncome = 0;
		float monthTax = 0;
		float yearIncome = 0;
		float yearTax = 0;

		for (Transaction t : allTransactions) {

			yearDates = transactionBusiness.getPaysInDateRange(t, yearStart, yearEnd);
			monthDates = transactionBusiness.getPaysInDateRange(t, monthStart, monthEnd);

			// Append Income forecast
			for (Date d : yearDates) {
				LocalDate l = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

				IncomeMonth month;

				if (!earningsMap.containsKey(l.getMonthValue())) {
					month = new IncomeMonth(l.getMonthValue(), l.getMonth().getDisplayName(TextStyle.FULL, Locale.US),
							0, 0, 0);
				} else {
					month = earningsMap.get(l.getMonthValue());
				}

				if (t.getCategory() == TransactionCategoryEnum.TAXES) {
					month.setTax(month.getTax() + t.getAmount());
				} else {
					month.setGrossIncome(month.getGrossIncome() + t.getAmount());
				}

				month.setNetIncome(month.getGrossIncome() - month.getTax());
				earningsMap.put(l.getMonthValue(), month);
			}

			if (t.getCategory() == TransactionCategoryEnum.TAXES) {
				monthTax += (float) monthDates.size() * t.getAmount();
				yearTax += (float) yearDates.size() * t.getAmount();

			} else {
				monthIncome += (float) monthDates.size() * t.getAmount();
				yearIncome += (float) yearDates.size() * t.getAmount();
			}

			if (monthDates.size() > 0) {
				monthSummary.items.add(IncomeFactory.buildIncomeSummaryItem(t, monthDates.size()));
			}
			
			if (yearDates.size() > 0) {
				annualSummary.items.add(IncomeFactory.buildIncomeSummaryItem(t, yearDates.size()));
			}
		}

		earningsForcast.addAll(earningsMap.values());

		monthSummary.setTotalIncome(monthIncome);
		monthSummary.setTotalTax(monthTax);
		annualSummary.setTotalTax(yearTax);
		annualSummary.setTotalIncome(yearIncome);

		response.setAnnualSummary(annualSummary);
		response.setMonthSummary(monthSummary);
		response.setMonth(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonth()
				.getDisplayName(TextStyle.FULL, Locale.US));
		response.setUserId(userId);
		response.setForecastedEarnings(earningsForcast);

		return response;
	}
}
