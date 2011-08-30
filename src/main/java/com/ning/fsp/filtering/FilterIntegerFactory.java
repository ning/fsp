package com.ning.fsp.filtering;

import com.google.common.base.Predicate;
import com.ning.fsp.FilterParameter;
import com.ning.fsp.util.Adapter;

/**
 * Factory returning criterias to match integer values.
 *
 * @param <T> Type of the filtered objects.
 */
public class FilterIntegerFactory<T> implements FilterCriteriaFactory<T> {

    private final boolean expensive;
    private final String columnName;
    private final Adapter<T, Integer> integerAdapter;

    /**
     * Factory that only returns 'expensive' filter criterias.
     */
    public FilterIntegerFactory(final Adapter<T, Integer> integerAdapter) {
        this.expensive = true;
        this.columnName = null;
        this.integerAdapter = integerAdapter;
    }

    /**
     * Factory that returns filters that can be run 'cheap' or 'expensive'.
     */
    public FilterIntegerFactory(final String columnName, final Adapter<T, Integer> integerAdapter) {
        this.expensive = false;
        this.columnName = columnName;
        this.integerAdapter = integerAdapter;
    }

    public FilterCriteria<T> getCriteria(final FilterParameter filterParameter) {

        final Integer match = parseNullableInteger(filterParameter.getFieldMatch());

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
                        final Integer value = integerAdapter.getValue(type);
                        if (match == null || value == null) {
                            return false;
                        }

                        return value.equals(match);
                    }
                };
            }

            @Override
            public String toString() {
                return String.format("IntegerFilterCriteria(match=%d, columnName=%s, including=%b)", match, columnName, isIncluding());
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

    private Integer parseNullableInteger(final String value) {

        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException nfe) {
            return null;
        }
    }
}


