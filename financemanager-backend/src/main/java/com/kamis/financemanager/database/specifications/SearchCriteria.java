package com.kamis.financemanager.database.specifications;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchCriteria {

	private String key;
	private Object value;
	private QueryOperation operation;
	private PredicateType type;
	
}
