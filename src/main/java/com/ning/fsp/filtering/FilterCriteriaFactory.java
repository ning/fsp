package com.ning.fsp.filtering;

import com.ning.fsp.FilterParameter;

/**
 * A factory to return filter criterias.
 *
 * @param <T> Type of the filtered objects.
 */
public interface FilterCriteriaFactory<T> {
    /**
     * Builds a full filter criteria from the filter parameters. Some of these parameters
     * might need to be set on the c'tor of the actual implementation.
     *
     * @param filterParameter The {@link FilterParameter} object representing the filter.
     * @return A Filter Criteria. This is an one-shot object that consumers should use and then discard.
     */
    FilterCriteria<T> getCriteria(FilterParameter filterParameter);
}
