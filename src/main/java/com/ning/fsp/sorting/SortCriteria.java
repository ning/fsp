package com.ning.fsp.sorting;

import com.google.common.collect.Ordering;

/**
 * A sort criteria is used to perform sorting on a list of elements. A sort can be done
 * 'cheap' by letting the database sort or 'expensive' where the unsorted list of elements
 * is sorted in memory.
 */
public interface SortCriteria<T>
{
    /**
     * Returns true if the sorting order is 'reversed' (descending).
     * @return True if the sorting order is descending:
     */
    boolean isDescending();

    /**
     * Returns true if null values for this field should be sorted first.
     *
     * @return True if null values are 'smaller' than any all values. False otherwise.
     */
    boolean isNullsFirst();

    /**
     * Returns true if the sort should be done in memory.
     *
     * @return True if the sort should be done in memory.
     */
    boolean isExpensive();

    /**
     * Returns an ordering that can be used for in-memory sorting.
     * @return An {@link Ordering} object.
     */
    Ordering<T> getOrdering();


    /**
     * The column name on which should be sorted.
     *
     * @return The column name to sort on.
     */
    String getColumnName();
}

