package com.kamis.financemanager.rest.domain.generic;

import lombok.Data;

/**
 * Meant to be extended by other responses
 */
@Data
public class GenericPagedItemResponse {

	private Integer page;

	private Integer pageSize;

	private Integer count;
}
