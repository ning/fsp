package com.ning.fsp.sorting;

import com.ning.fsp.SortParameter;

/**
 * A factory to return sorting criterias.
 *
 * @param <T> Type of the sorted objects.
 */
public interface SortCriteriaFactory<T> {

    /**
     * Builds a full sort criteria from the sort parameters.
     *
     * @param sortParameter Sort parameters to select a sorting criteria.
     *
     * @return A SortCriteria to perform the sort.
     */
    SortCriteria<T> getCriteria(SortParameter sortParameter);
}
