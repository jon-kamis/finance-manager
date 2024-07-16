package com.kamis.financemanager.rest.domain.loans;

import java.util.List;

import com.kamis.financemanager.rest.domain.generic.GenericPagedItemResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class PagedLoanResponse extends GenericPagedItemResponse {

	private List<LoanResponse> items;

}
