package com.ning.fsp;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ning.fsp.filtering.FilterCriteria;
import com.ning.fsp.filtering.FilterCriteriaFactory;
import com.ning.fsp.util.Pair;
import static com.ning.fsp.util.Pair.pair;


/**
 * All the filtering of result sets is done in a filter. For every query, a set of FilterParameters is
 * registered with the filter which then calculates whether the filter can be offloaded to the database
 * or must be run in-memory.
 *
 * @param <T>
 */
public class Filter<T> {
    public enum FilterCost {
        CHEAP, EXPENSIVE;
    }

    private final FilterCost filterCost;

    private final Map<String, FilterCriteriaCollector<T>> cheapFilters = Maps.newHashMap();
    private final Map<String, FilterCriteriaCollector<T>> expensiveFilters = Maps.newHashMap();

    private boolean cheap = false;
    private boolean expensive = false;

    /**
     * Constructs a new Filter.
     * @param filterParameters The parameters to filter on.
     * @param criteriaMap A map of know criterias to filter and factories to create matching criterias.
     */
    public Filter(final List<? extends FilterParameter> filterParameters, final Map<String, FilterCriteriaFactory<T>> criteriaMap) {
        this(filterParameters, criteriaMap, FilterCost.CHEAP);
    }

    public Filter(final List<? extends FilterParameter> filterParameters, final Map<String, FilterCriteriaFactory<T>> criteriaMap, final FilterCost filterCost) {

        this.filterCost = filterCost;

        if (CollectionUtils.isNotEmpty(filterParameters)) {
            for (FilterParameter filterParameter : filterParameters) {
                final String fieldName = filterParameter.getFieldName();
                final FilterCriteriaFactory<T> filterCriteriaFactory = criteriaMap.get(fieldName);
                if (filterCriteriaFactory == null) {
                    throw new IllegalArgumentException("Field '" + fieldName + "' is not valid for filtering!");
                }

                put(fieldName, filterCriteriaFactory.getCriteria(filterParameter));
            }
        }
    }

    /**
     * Add an additional criteria to the filter.
     * @param fieldName The field to filter on.
     * @param criteria The criteria to use.
     */
    public synchronized void put(final String fieldName, final FilterCriteria<T> criteria)
    {
        // We treat "excluding" as "expensive" for now.
        final boolean criteriaIsExpensive =  criteria.isExpensive() || !criteria.isIncluding();

        Map<String, FilterCriteriaCollector<T>> filterMap;
        if (filterCost == FilterCost.EXPENSIVE || criteriaIsExpensive) {
            expensive = true;
            filterMap = expensiveFilters;
        }
        else {
            cheap = true;
            filterMap = cheapFilters;
        }

        FilterCriteriaCollector<T> collector = filterMap.get(fieldName);
        if (collector == null) {
            filterMap.put(fieldName, new FilterCriteriaCollector<T>(criteria));
        }
        else {
            collector.add(criteria);
        }
    }

    /**
     * Returns true if the filter contains expensive (i.e. in-memory) filters.
     * @return True if at least one filter needs to run in-memory.
     */
    public synchronized boolean isExpensive() {
        return expensive;
    }

    /**
     * Returns true if the filter contains at least one cheap (i.e. database) filter.
     * @return True if at least one filter can be executed by the database.
     */
    public synchronized boolean isCheap() {
        return cheap;
    }

    /**
     * Returns all cheap filters. They can be run by the database.
     */

    public Collection<FilterCriteriaCollector<T>> getCheapFilters() {
        return cheapFilters.values();
    }

    /**
     * Filter a collection of elements according to the registered FilterCriterias. This
     * method only executes the expensive filters; it assumes that all cheap filters were
     * run before the list is passed into this method.
     *
     * @param elements A list of elements to filter.
     * @return The filtered list of elements. This can be the same list, a subset or an empty list. It is never null.
     */
    public Collection<T> filter(final Collection<T> elements) {

        if (!isExpensive()) {
            return elements;
        }

        final Predicate<T> predicate = Predicates.and(Collections2.transform(expensiveFilters.values(), new Function<FilterCriteriaCollector<T>, Predicate<T>>() {
            @Override
            public Predicate<T> apply(final FilterCriteriaCollector<T> collector) {
                return collector.getPredicate();
            }
        }));

        return Collections2.filter(elements, predicate);
    }

    /**
     * Do a streaming filter according to the registered FilterCriterias. This
     * method only executes the expensive filters; it assumes that all cheap filters were
     * run before the list is passed into this method.
     *
     * @param elements A list of elements to filter.
     * @return An iterable representing the filtered list of elements. This can be the same list, a subset or an empty list. It is never null.
     */
    public Iterable<T> filter(final Iterable<T> elements) {

        if (!isExpensive()) {
            return elements;
        }

        final Predicate<T> predicate = Predicates.and(Collections2.transform(expensiveFilters.values(), new Function<FilterCriteriaCollector<T>, Predicate<T>>() {
            @Override
            public Predicate<T> apply(final FilterCriteriaCollector<T> collector) {
                return collector.getPredicate();
            }
        }));

        return Iterables.filter(elements, predicate);
    }


    public static final class FilterCriteriaCollector<T> {

        private final String columnName;
        private final boolean expensive;
        private final Object match;

        private final List<Pair<Integer, Object>> matches = Lists.newArrayList();

        private Predicate<T> includePredicate = null;
        private Predicate<T> excludePredicate = null;
        private int count = 0;

        public FilterCriteriaCollector(final FilterCriteria<T> criteria) {
            columnName = criteria.getColumnName();
            // We treat excluding criterias as expensive because the SQL gets translated to an IN ( ... ) clause, something
            // that does not work well with "everything but ... " expressions.

            // TODO: This might either need to be fixed or we need to bite the bullet at some point and turn this
            //       into code that uses a 'real' O/R mapper.
            expensive =  criteria.isExpensive() || !criteria.isIncluding();
            match = criteria.getMatch();

            add(criteria);
        }

        public String getColumnName() {
            return columnName;
        }

        public synchronized void add(final FilterCriteria<T> criteria) {

           // This is used only for the cheap branch (which in turn does not use the inclusion / exclusion logic.
            matches.add(pair(count++, criteria.getMatch()));

            final Predicate<T> newPredicate = criteria.getPredicate();

            if (criteria.isIncluding()) {
                this.includePredicate = includePredicate == null ? newPredicate : Predicates.<T>or(includePredicate, newPredicate);
            }
            else {
                this.excludePredicate = excludePredicate == null ? newPredicate : Predicates.<T>or(excludePredicate, newPredicate);
            }
        }

        public boolean isExpensive() {
            return expensive;
        }

        public boolean isSingle() {
            return matches.size() == 1;
        }

        public Object getMatch() {
            return match;
        }

        public Collection<Pair<Integer, Object>> getMatches() {
            return matches;
        }

        public synchronized Predicate<T> getPredicate() {
            return Predicates.<T>and((includePredicate != null ? includePredicate : Predicates.<T>alwaysTrue()),
                                     Predicates.not((excludePredicate != null ? excludePredicate : Predicates.<T>alwaysFalse())));
        }
    }
}

