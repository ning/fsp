package com.ning.fsp.filtering;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.ning.fsp.FilterParameter;
import com.ning.fsp.util.Adapter;
import com.ning.fsp.util.StringMatchType;


/**
 *  Factory returning criterias to match string values.
 *
 * @param <T> Type of the filtered objects.
 */
public class FilterStringFactory<T> implements FilterCriteriaFactory<T> {

    private final StringMatchType matchType;

    private final boolean expensive;
    private final String columnName;
    private final Adapter<T, String> stringAdapter;

    /**
     * Factory that creates String filter based on object compares.
     */
    public FilterStringFactory(final StringMatchType matchType, final Adapter<T, String> stringAdapter) {
        this.matchType = matchType;

        this.expensive = true;
        this.columnName = null;
        this.stringAdapter = stringAdapter;
    }

    /**
     * Factory that creates String filter based on database compares. The filter
     * can fall back to expensive object compares.
     */
    public FilterStringFactory(final StringMatchType matchType, final String columnName, final Adapter<T, String> stringAdapter) {
        this.matchType = matchType;

        this.expensive = false;
        this.columnName = columnName;
        this.stringAdapter = stringAdapter;
    }

    public FilterCriteria<T> getCriteria(final FilterParameter filterParameter) {

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
            public boolean isIncluding() {
                return filterParameter.isIncluding();
            }

            @Override
            public Predicate<T> getPredicate() {
                return new Predicate<T>() {
                    @Override
                    public boolean apply(final T type) {
                        final String value = stringAdapter.getValue(type);

                        switch (matchType) {
                        case CASE_SENSITIVE_EXACT:
                            return StringUtils.equals(value, filterParameter.getFieldMatch());
                        case CASE_INSENSITIVE_EXACT:
                            return StringUtils.equalsIgnoreCase(value, filterParameter.getFieldMatch());
                        case CASE_SENSITIVE_PARTIAL:
                            return StringUtils.contains(value, filterParameter.getFieldMatch());
                        case CASE_INSENSITIVE_PARTIAL:
                            return StringUtils.containsIgnoreCase(value, filterParameter.getFieldMatch());
                        default:
                            throw new IllegalArgumentException(String.format("Found unknown match type %s", matchType));
                        }
                    }
                };
            }

            @Override
            public Object getMatch() {
                return filterParameter.getFieldMatch();
            }

            @Override
            public String toString() {
                return String.format("StringFilterCriteria(match=%s, columnName=%s, including=%b)", filterParameter.getFieldMatch(), columnName, isIncluding());
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
}


