package com.kamis.financemanager.rest.domain.incomes;

import java.util.List;

import lombok.Data;

@Data
public class PagedIncomeResponse {

	public List<IncomeResponse> items;
	public Integer page;
	public Integer pageSize;
	public Integer count;
}
