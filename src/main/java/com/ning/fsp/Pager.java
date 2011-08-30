package com.ning.fsp;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

/**
 * The pager handles all the paging of result sets. It should be smart enough that, when
 * the filter sets are all cheap (i.e. run on the db), the paging should also be done in
 * the db, but for now, it does not.
 *
 * TODO fix paging to be run in the DB if possible.
 *
 * @param <T> The Element type to page on.
 */
public class Pager<T> {

    public enum PagerCost {
        /** Can do both lower and upper end in the DB */
        CHEAP,
        /** Must do both lower and upper end in memory */
        EXPENSIVE,
    }

    private final Integer start;
    private final Integer size;
    private final PagerCost cost;

    private final Filter<T> filter;
    private final Sorter<T> sorter;

    public Pager(final PagerParameter pagerParams, final Filter<T> filter, final Sorter<T> sorter) {
        this(pagerParams, Pager.getCost(filter, sorter), filter, sorter);
    }

    public Pager(final PagerParameter pagerParams, final PagerCost cost) {
        this(pagerParams, cost, null, null);
    }

    private Pager(final PagerParameter pagerParams, final PagerCost cost, final Filter<T> filter, final Sorter<T> sorter) {
        Integer start = pagerParams.getKey();
        Integer size = pagerParams.getValue();

        // If a size was given but no start, we assume start at the beginning.
        if (start == null && size != null) {
            start = 0;
        }

        this.start = start;
        this.size = size;
        this.cost = cost;
        this.filter = filter;
        this.sorter = sorter;
    }

    private static PagerCost getCost(final Filter<?> filter, final Sorter<?> sorter) {
        PagerCost cost = PagerCost.CHEAP;

        // As soon as we either have to filter or to sort in memory, we can't page in the DB.
        if (filter != null && filter.isExpensive()) {
            cost = PagerCost.EXPENSIVE;
        }
        else if (sorter != null && !sorter.isCheap()) {
            cost = PagerCost.EXPENSIVE;
        }

        return cost;
    }

    /**
     * Returns the upper bound if the DB pager should set an upper bound.
     *
     * This requires that the pager is
     * cheap and we actually have an upper bound. If there is none, don't bother to add
     * one.
     *
     * @return The value for the upper bound or null if none should be set.
     */
    public Integer getUpperBound() {
        if (cost == PagerCost.CHEAP && size != null) {
            return start + 1 + size; // Like lowerbound, we must inc by 1 for 1 based SQL query.
        }
        else {
            return null;
        }
    }

    /**
     * True if the DB pager should set a lower bound. This requires that the page is
     * not really expensive (in which case we don't bother) and also the start is not
     * 0 (or null).
     *
     * @return
     */
    public Integer getLowerBound() {
        if (cost == PagerCost.CHEAP && (start != null && start != 0)) {
            return start + 1;
        }
        else {
            return null;
        }
    }

    public Integer getStart() {
        return start;
    }

    public Integer getSize() {
        return size;
    }

    public Filter<T> getFilter()
    {
        return filter;
    }

    public Sorter<T> getSorter()
    {
        return sorter;
    }

    public Collection<T> page(final Collection<T> elements) {

        // If we don't have a valid start position or consider the actual paging
        // cheap enough to be done in the db, we don't bother.
        if (start == null || cost == PagerCost.CHEAP) {
            return elements;
        }

        return Collections2.filter(elements, new PagerPredicate());
    }

    public Iterable<T> page(final Iterable<T> elements) {

        // If we don't have a valid start position or consider the actual paging
        // cheap enough to be done in the db, we don't bother.
        if (start == null || cost == PagerCost.CHEAP) {
            return elements;
        }

        return Iterables.filter(elements, new PagerPredicate());
    }

    private class PagerPredicate implements Predicate<T>
    {
        private Integer window = size;

        private int count = 0;

        @Override
        public boolean apply(final T element)
        {
            // If paging is really expensive, we also must move to the first element
            if (count++ < start) {
                // If we actually run the filter, we have a start element.
                // This is 0 based
                return false;
            }
            // Either we have no window size (in that case all of the
            // following elements are returned or a slowly closing window,
            // which returns elements as long as the window is not 0.
            else if ((window == null) || (window-- > 0)) {
                return true;
            }
            else {
                return false;
            }
        }
    }
}
