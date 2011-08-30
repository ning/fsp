package com.ning.fsp.filtering;

import com.google.common.base.Predicate;

/**
 * A filter criteria is used to perform filtering on a list of elements. A filter can be
 * 'cheap' in which case it is performed by the DAO and influences the database query or
 * 'expensive'. An expensive filter is run by the actual filter object in memory on a larger
 * subset.
 *
 * @param <T> The element type to filter.
 */
public interface FilterCriteria<T>
{
    /**
     * Returns true if this criteria must be executed in memory and not by the database.
     *
     * @return True if this filter must be run in memory.
     */
    boolean isExpensive();

    /**
     * True if this filter includes matches. False if it excludes matches.
     */
    boolean isIncluding();

    /**
     * Returns the name of the database column to filter on when this criteria is executed
     * as a cheap filter.
     *
     * @return Name of the database column to use.
     */
    String getColumnName();

    /**
     * Returns the matching Object.
     */
    Object getMatch();

    /**
     * Returns a Predicate to determine whether a given object matches the criteria or not.
     *
     * @return A predicate for matching elements to the criteria. This predicate is used for expensive, in-memory filtering.
     */
    Predicate<T> getPredicate();
}

