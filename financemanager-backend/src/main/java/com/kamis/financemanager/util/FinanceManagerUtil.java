package com.kamis.financemanager.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.database.domain.AuditInfo;
import com.kamis.financemanager.enums.WeekdayEnum;

public class FinanceManagerUtil {

	/**
	 * Generates a new AuditInfo object and returns it
	 * 
	 * @return a newly initialized AuditInfo object
	 */
	public static AuditInfo getAuditInfo() {
		AuditInfo auditInfo = new AuditInfo();
		auditInfo.setCreateDt(new Date());
		auditInfo.setLastUpdateDt(new Date());
		auditInfo.setLastUpdateBy(getLoggedInUserName());
		return auditInfo;
	}

	public static String getLoggedInUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		return currentPrincipalName;
	}

	/**
	 * Builds a standard Sort object from sorting parameters
	 * 
	 * @param sortBy   Determines which field to sort by
	 * @param sortType Determines the sorting direction
	 * @return A new Sort object built from the given criteria
	 */
	public static Sort buildSort(String sortBy, String sortType) {
		Sort sort = null;

		// Sorting direction
		boolean sortAsc = sortType == null || sortType.isBlank()
				|| sortType.equalsIgnoreCase(FinanceManagerConstants.SORT_TYPE_ASC);

		if (sortBy != null && !sortBy.isBlank()) {

			sort = sortAsc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		}

		return sort;
	}

	/**
	 * Builds a standard Pageable object from pagination parameters
	 * 
	 * @param page     The page to retrieve
	 * @param pageSize The size of pages to retrieve
	 * @param sort     Optional sorting parameters
	 * @return A new Pageable built from the given criteria, or null if paging
	 *         should not be done
	 */
	public static Pageable buildPageable(Integer page, Integer pageSize, Sort sort) {

		Pageable pageable = null;

		if (page == null || page < 1) {
			page = 1;
		}

		// Build pageable. Note users will enter pages starting at 1 but it is 0
		// indexed, so we subtract 1 when building
		// pageable objects
		if (pageSize != null && pageSize >= 1 && sort != null) {
			pageable = PageRequest.of(page - 1, pageSize, sort);
		} else if (pageSize != null && pageSize >= 1) {
			pageable = PageRequest.of(page - 1, pageSize);
		}

		return pageable;
	}

	/**
	 * Returns a count of the number of a specific weekDate for a given month
	 * 
	 * @param weekDay The weekday to search for
	 * @param date    A LocalDate containing the month to search in
	 * @return An int representing the number of occurrences of the specific week
	 *         date for the given month
	 */
	public static int getWeekdayOccurancesForMonth(WeekdayEnum weekDay, LocalDate date) {
		LocalDate start = getFirstWeekDateInMonth(weekDay, date);
		LocalDate end = start.plusMonths(1);
		int count = 0;

		while (start.isBefore(end)) {
			count++;
			start.plusWeeks(1);
		}

		return count;
	}

	/**
	 * Gets the first occurrence of a specific week date for a month
	 * 
	 * @param weekDay   The weekday to search for
	 * @param localDate The date containing the month to search
	 * @return A LocalDate representing the first occurrence of weekDay for the
	 *         given month
	 */
	public static LocalDate getFirstWeekDateInMonth(WeekdayEnum weekDay, LocalDate localDate) {
		localDate = localDate.withDayOfMonth(1);

		DayOfWeek targetDay = DayOfWeek.of(weekDay.getDayIndex());

		while (localDate.getDayOfWeek() != targetDay) {
			localDate.plusDays(1);
		}

		return localDate;
	}

	/**
	 * Returns the first day of the year date resides in
	 * 
	 * @param date A date containing the year to get the first day of
	 * @return The start of the year date resides in
	 */
	public static Date getStartOfYear(Date date) {
		LocalDate lDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return Date.from(LocalDate.of(lDate.getYear(), Month.JANUARY, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	/**
	 * Returns the end of the year date resides in
	 * 
	 * @param date A date containing the year to get the first day of
	 * @return The end of the year date resides in
	 */
	public static Date getEndOfYear(Date date) {
		LocalDate lDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return Date.from(LocalDate.of(lDate.getYear() + 1, Month.JANUARY, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusNanos(1));
	}
	
	/**
	 * Returns the start of the month date resides in
	 * 
	 * @param date A date containing the year and month to get the first day of
	 * @return The start of the month date resides in
	 */
	public static Date getStartOfMonth(Date date) {
		LocalDate lDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return Date.from(LocalDate.of(lDate.getYear(), lDate.getMonth(), 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	/**
	 * Returns the end of the month date resides in
	 * 
	 * @param date A date containing the year and month to get the first day of
	 * @return The end of the month date resides in
	 */
	public static Date getEndOfMonth(Date date) {
		LocalDate lDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return Date.from(LocalDate.of(lDate.getYear(), lDate.getMonth().plus(1), 1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusNanos(1));
	}
}
