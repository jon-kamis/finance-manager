package com.kamis.financemanager.database.specifications;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenericSpecification<T> {

	private final List<SearchCriteria> params;

	public GenericSpecification() {
		params = new ArrayList<>();
	}

	/**
	 * Adds a new Search criteria manually
	 * 
	 * @param key       The search criteria's key
	 * @param value     The search criteria's value
	 * @param operation The search criteria's operation
	 * @param type      The type of predicate to add
	 */
	public void add(String key, Object value, QueryOperation operation, PredicateType type) {
		params.add(new SearchCriteria(key, value, operation, type));
	}

	/**
	 * Starts a new query by building a search criteria with no predicate type
	 * 
	 * @param key       the new search criteria's key
	 * @param value     The new search criteria's value
	 * @param operation The new search criteria's operation
	 * @return this GenericSpecification with the new searchCriteria appended as an
	 *         and predicate
	 */
	public GenericSpecification<T> where(String key, Object value, QueryOperation operation) {
		params.add(new SearchCriteria(key, value, operation, null));
		return this;
	}

	/**
	 * Adds a new Search Criteria as an AND predicate
	 * 
	 * @param key       the new search criteria's key
	 * @param value     The new search criteria's value
	 * @param operation The new search criteria's operation
	 * @return this GenericSpecification with the new searchCriteria appended as an
	 *         AND predicate
	 */
	public GenericSpecification<T> and(String key, Object value, QueryOperation operation) {
		params.add(new SearchCriteria(key, value, operation, PredicateType.AND));
		return this;
	}

	/**
	 * Adds a new Search Criteria as an OR predicate
	 * 
	 * @param key       the new search criteria's key
	 * @param value     The new search criteria's value
	 * @param operation The new search criteria's operation
	 * @return this GenericSpecification with the new searchCriteria appended as an
	 *         OR predicate
	 */
	public GenericSpecification<T> or(String key, Object value, QueryOperation operation) {
		params.add(new SearchCriteria(key, value, operation, PredicateType.OR));
		return this;
	}

	/**
	 * Buils a JPA specification based on the search criteria this object contains
	 * 
	 * @return this GenericSpecification with the new searchCriteria appended as an
	 *         AND predicate
	 */
	public Specification<T> build() {
		if (params.isEmpty()) {
			return null;
		}

		List<Specification<T>> specs = params.stream().map(this::buildSpecification).toList();

		Specification<T> spec = specs.get(0);

		log.debug("building generic specification where");
		log.debug("{} {} {}", params.getFirst().getKey(), params.getFirst().getOperation(),
				params.getFirst().getValue().toString());

		// Build and chain specification objects
		for (int i = 1; i < params.size(); i++) {
			log.debug("{} {} {} {}", params.get(i).getType(), params.get(i).getKey(), params.get(i).getOperation(),
					params.get(i).getValue() != null ? params.get(i).getValue().toString() : "NULL");

			spec = params.get(i).getType() == PredicateType.AND ? Specification.where(spec).and(specs.get(i))
					: Specification.where(spec).or(specs.get(i));
		}

		return spec;
	}

	public Specification<T> buildSpecification(SearchCriteria criteria) {

        return new Specification<>() {

			@Serial
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return buildCriteria(criteria, root, criteriaBuilder);
			}

		};
	}

	@SuppressWarnings("unchecked")
	private Predicate buildCriteria(SearchCriteria criteria, Root<T> root, CriteriaBuilder builder) {
        return switch (criteria.getOperation()) {
            case EQUALS -> builder.equal(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            case EQUALS_OBJECT -> builder.equal(root.<String>get(criteria.getKey()), criteria.getValue());
            case CONTAINS -> builder.like(root.<String>get(criteria.getKey()),
                    SpecConstants.LIKE_STR + criteria.getValue().toString() + SpecConstants.LIKE_STR);
            case NOT_CONTAINS -> builder.notLike(root.<String>get(criteria.getKey()),
                    SpecConstants.LIKE_STR + criteria.getValue().toString() + SpecConstants.LIKE_STR);
            case ENDS_WITH -> builder.like(root.<String>get(criteria.getKey()),
                    SpecConstants.LIKE_STR + criteria.getValue().toString());
            case GREATER_THAN ->
                    builder.greaterThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            case GREATER_THAN_EQUAL_TO ->
                    builder.greaterThanOrEqualTo(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN_EQUAL_TO ->
                    builder.lessThanOrEqualTo(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            case NOT_EQUALS -> builder.notEqual(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            case NOT_EQUALS_OBJECT -> builder.notEqual(root.<String>get(criteria.getKey()), criteria.getValue());
            case STARTS_WITH -> builder.like(root.<String>get(criteria.getKey()),
                    criteria.getValue().toString() + SpecConstants.LIKE_STR);
            case IS_NULL -> builder.isNull(root.get(criteria.getKey()));
            case IS_NOT_NULL -> builder.isNotNull(root.get(criteria.getKey()));
            case IN -> root.get(criteria.getKey()).in((List<Object>) criteria.getValue());
            case NOT_IN -> builder.not(root.get(criteria.getKey()).in((List<Object>) criteria.getValue()));
            default -> null;
        };

	}

}
