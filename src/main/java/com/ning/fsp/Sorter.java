package com.ning.fsp;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.ning.fsp.sorting.SortCriteria;
import com.ning.fsp.sorting.SortCriteriaFactory;


/**
 * All the sorting is done using a sorter. For every sort, a set of SortParameters is registered with the sorter. The sorter decides
 * whether it can run the sort in memory or in the database. All sorts must support both sorting ways, because the first non-cheap
 * sort ends what sorts can be done by the DB.
 *
 * @author henning
 * @param <T>
 */
public class Sorter<T>
{
    public enum SorterCost {
        CHEAP, EXPENSIVE;
    }

    private final SorterCost sorterCost;

    private final List<SortCriteria<T>> criterias = Lists.newArrayList();

    public Sorter(final List<? extends SortParameter> sortParameters,
                  final Map<String, SortCriteriaFactory<T>> criteriaMap)
    {
        this(sortParameters, criteriaMap, SorterCost.CHEAP);
    }

    public Sorter(final List<? extends SortParameter> sortParameters,
                  final Map<String, SortCriteriaFactory<T>> criteriaMap,
                  final SorterCost sorterCost)
    {

        this.sorterCost = sorterCost;

        if (CollectionUtils.isNotEmpty(sortParameters)) {
            for (SortParameter sortParameter : sortParameters) {
                final SortCriteriaFactory<T> sortCriteriaFactory = criteriaMap.get(sortParameter.getFieldName());
                if (sortCriteriaFactory == null) {
                    throw new IllegalArgumentException("Field '" + sortParameter.getFieldName() + "' is not valid for sorting!");
                }
                criterias.add(sortCriteriaFactory.getCriteria(sortParameter));
            }
        }
    }

    public void add(final SortCriteria<T> criteria)
    {
        criterias.add(criteria);
    }

    /**
     * Test whether the sorts can be run 'cheap'. As sorts are not commutative and mixing "cheap" and "expensive" sorts is messy, we
     * consider this "expensive" as soon as a single sort is expensive.
     *
     * @return
     */
    public boolean isCheap()
    {
        if (sorterCost == SorterCost.EXPENSIVE) {
            return false;
        }

        // Nothing to sort, so we are cheap.
        if (criterias.size() == 0) {
            return true;
        }

        for (SortCriteria<T> criteria : criterias) {
            if (criteria.isExpensive()) {
                return false;
            }
        }
        return true;
    }

    public boolean isSort()
    {
        return isCheap() && criterias.size() > 0;
    }

    public List<SortCriteria<T>> getCriterias()
    {
        return Collections.unmodifiableList(criterias);
    }

    public Collection<T> sort(final Collection<T> elements)
    {
        if (isCheap()) {
            return elements;
        }
        else {
            final Ordering<T> orderer = getOrderer(criterias);
            return orderer.sortedCopy(elements);
        }
    }

    public Iterable<T> sort(final Iterable<T> elements)
    {
        if (isCheap()) {
            return elements;
        }
        else {
            final Ordering<T> orderer = getOrderer(criterias);
            return orderer.sortedCopy(elements);
        }
    }

    private Ordering<T> getOrderer(final List<SortCriteria<T>> criterias) {
        return Ordering.compound(Lists.transform(criterias, new Function<SortCriteria<T>, Ordering<T>>() {
            @Override
            public Ordering<T> apply(final SortCriteria<T> criteria)
            {
                final Ordering<T> orderer = criteria.getOrdering();
                return criteria.isDescending() ? orderer.reverse() : orderer;
            }
        }));
    }
}
