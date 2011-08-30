package com.ning.fsp.filtering;

import com.google.common.base.Predicate;
import com.ning.fsp.FilterParameter;
import com.ning.fsp.util.Adapter;

/**
 * Factory returning criterias to match long values.
 *
 * @param <T> Type of the filtered objects.
 */
public class FilterLongFactory<T> implements FilterCriteriaFactory<T> {

    private final boolean expensive;
    private final String columnName;
    private final Adapter<T, Long> longAdapter;

    /**
     * Factory that only returns 'expensive' filter criterias.
     */
    public FilterLongFactory(final Adapter<T, Long> longAdapter) {
        this.expensive = true;
        this.columnName = null;
        this.longAdapter = longAdapter;
    }

    /**
     * Factory that returns filters that can be run 'cheap' or 'expensive'.
     */
    public FilterLongFactory(final String columnName, final Adapter<T, Long> longAdapter) {
        this.expensive = false;
        this.columnName = columnName;
        this.longAdapter = longAdapter;
    }

    public FilterCriteria<T> getCriteria(final FilterParameter filterParameter) {

        final Long match = parseNullableLong(filterParameter.getFieldMatch());

        return new FilterCriteria<T>() {

            @Override
            public String getColumnName() {
                return columnName;
            }

            @Override
            public boolean isExpensive() {
                return expensive;
            }

            @Override
            public Object getMatch() {
                return match;
            }

            @Override
            public boolean isIncluding() {
                return filterParameter.isIncluding();
            }

            @Override
            public Predicate<T> getPredicate() {
                return new Predicate<T>() {
                    @Override
                    public boolean apply(final T type) {
                        final Long value = longAdapter.getValue(type);
                        if (match == null || value == null) {
                            return false;
                        }

                        return value.equals(match);
                    }
                };
            }

            @Override
            public String toString() {
                return String.format("LongFilterCriteria(match=%d, columnName=%s, including=%b)", match, columnName, isIncluding());
            }

            @Override
            public boolean equals(final Object o) {
                return this == o;
            }

            @Override
            public int hashCode() {
                return System.identityHashCode(this);
            }
        };
    }

    private Long parseNullableLong(final String value) {

        try {
            return Long.parseLong(value);
        }
        catch (NumberFormatException nfe) {
            return null;
        }
    }
}


