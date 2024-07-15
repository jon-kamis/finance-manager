package com.kamis.financemanager.util;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kamis.financemanager.database.domain.AuditInfo;

public class FinanceManagerUtil {

	/**
	 * Generates a new AuditInfo object and returns it
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
}
