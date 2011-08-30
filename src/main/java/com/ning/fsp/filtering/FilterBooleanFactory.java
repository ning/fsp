package com.ning.fsp.filtering;

import org.apache.commons.lang.BooleanUtils;

import com.google.common.base.Predicate;
import com.ning.fsp.FilterParameter;
import com.ning.fsp.util.Adapter;


/**
 * Factory returning criterias to match boolean values.
 *
 * @param <T> Type of the filtered objects.
 */
public class FilterBooleanFactory<T> implements FilterCriteriaFactory<T> {

    private final boolean expensive;
    private final String columnName;
    private final Adapter<T, Boolean> booleanAdapter;

    public FilterBooleanFactory(final Adapter<T, Boolean> booleanAdapter) {
        this.expensive = true;
        this.columnName = null;
        this.booleanAdapter = booleanAdapter;
    }

    public FilterBooleanFactory(final String columnName, final Adapter<T, Boolean> booleanAdapter) {
        this.expensive = false;
        this.columnName = columnName;
        this.booleanAdapter = booleanAdapter;
    }

    public FilterCriteria<T> getCriteria(final FilterParameter filterParameter) {

        final Boolean match = parseNullableBoolean(filterParameter.getFieldMatch());

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
                        final Boolean value = booleanAdapter.getValue(type);
                        if (match == null || value == null) {
                            return false;
                        }
                        return value.equals(match);
                    }
                };
            }

            @Override
            public String toString() {
                return String.format("BooleanFilterCriteria(match=%b, columnName=%s, including=%b)", match, columnName, isIncluding());
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

    private Boolean parseNullableBoolean(final String value) {

        if (value == null)  {
            // (findbugs complains about NP here, that a method with a boolean return value returns null. Findbugs is wrong, the code is correct. Returning null
            // is the whole point of this method.)
            return (Boolean) null;
        }
        return BooleanUtils.toBoolean(value) || "1".equals(value);
    }
}


